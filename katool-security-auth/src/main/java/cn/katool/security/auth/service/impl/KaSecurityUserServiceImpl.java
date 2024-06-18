package cn.katool.security.auth.service.impl;

import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.katool.security.auth.constant.DateUnit;
import cn.katool.security.auth.exception.BusinessException;
import cn.katool.security.auth.exception.ErrorCode;
import cn.katool.security.auth.model.dto.user.KaSecurityUserQueryRequest;
import cn.katool.security.auth.model.entity.KaSecurityLoginLog;
import cn.katool.security.auth.model.entity.KaSecurityUser;
import cn.katool.security.auth.model.graph.IncGraphNode;
import cn.katool.security.auth.model.vo.kauser.KaSecurityLoginUserVO;
import cn.katool.security.auth.model.vo.kauser.KaSecurityUserVO;
import cn.katool.security.auth.service.KaSecurityLoginLogService;
import cn.katool.security.auth.utils.AesUtils;
import cn.katool.security.auth.utils.SqlUtils;
import cn.katool.security.core.constant.KaSecurityConstant;
import cn.katool.security.starter.utils.KaSecurityAuthUtil;
import cn.katool.util.auth.AuthUtil;
import cn.katool.util.database.nosql.RedisUtils;
import cn.katool.util.verify.IPUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.katool.security.auth.service.KaSecurityUserService;
import cn.katool.security.auth.mapper.KaSecurityUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import sun.misc.BASE64Decoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author 30398
* @description 针对表【ka_security_user】的数据库操作Service实现
* @createDate 2023-12-28 00:41:40
*/
@Slf4j
@Service
public class KaSecurityUserServiceImpl extends ServiceImpl<KaSecurityUserMapper, KaSecurityUser>
    implements KaSecurityUserService{

    @Resource
    KaSecurityLoginLogService kaSecurityLoginLogService;

    KaSecurityAuthUtil<KaSecurityUser> authUtil = new KaSecurityAuthUtil<KaSecurityUser>();
    public static final String SALT = "KA_SECURITY_USER_SALT:";

    @Override
    public String getUUID(HttpServletRequest request) {
        if (ObjectUtils.isEmpty(request)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR);
        }
        String uuid = request.getHeader("uuid").trim();
        if (StringUtils.isBlank(uuid)){
            uuid = request.getHeader("Uuid").trim();
        }
        return uuid;
    }

    @Resource
    RedisUtils redisUtils;
    @Override
    public String decrypt(String kaSecurityEntryPassWord, String uuid) {
        String secret = (String) redisUtils.getMap("LOGIN:SECRETPAIR", uuid);
        String decrypt = AesUtils.decrypt(kaSecurityEntryPassWord, secret);
        return decrypt;
    }

    @Override
    public String generateKeyPairAndReturnPublicKey() {
        RSA rsa = new RSA();
        String publicKey = rsa.getPublicKeyBase64();
        String privateKey = rsa.getPrivateKeyBase64();
        redisUtils.pushMap("LOGIN:RSAPAIR",publicKey,privateKey);
        return publicKey;
    }

    /**
     * 用户注销
     *
     * @param token
     */
    @Override
    public boolean userLogout(String token) {
        if (AuthUtil.getPayLoadFromToken(token,KaSecurityUser.class) == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        String primary = authUtil.getPayLoadPrimary(token,KaSecurityUser.class);
        Boolean logout = authUtil.logout(primary, token);
        return logout;
    }
    @Override
    public String getUUID(String pub, String hexMsg) {
        RSA rsa = new RSA();
        String pvbBase64 = (String) redisUtils.getMap("LOGIN:RSAPAIR", pub);
        PrivateKey pvb = null;
        try {
            byte[] keyBytes = (new BASE64Decoder()).decodeBuffer(pvbBase64);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            pvb = keyFactory.generatePrivate(keySpec);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
        rsa.setPrivateKey(pvb);
        byte[] decrypt = rsa.decrypt(hexMsg, KeyType.PrivateKey);
        String secret = new String(decrypt);
        String res = AesUtils.touchUUid(pub);
        redisUtils.pushMap("LOGIN:SECRETPAIR", res,secret);
        return res;
    }

    @Override
    public KaSecurityUserVO doLogin(String kaSecurityUserName, String kaSecurityUserPassWord, HttpServletRequest request, HttpServletResponse response) {
        // 1. 校验
        if (StringUtils.isAnyBlank(kaSecurityUserName, kaSecurityUserPassWord)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 2. 加密
        String encryptPassWord = getEntryPassWord(kaSecurityUserPassWord);
        // 查询用户是否存在
        QueryWrapper<KaSecurityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", kaSecurityUserName);
        queryWrapper.eq("pass_word", encryptPassWord);
        KaSecurityUser user = this.baseMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
           log.info("user login failed, kaSecurityUserName cannot match kaSecurityUserPassWord");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        log.info("jwt creating ...");
        String token = authUtil.login(user, KaSecurityUser.class);
        log.info("user:{} jsonwebToken:{}",user,token);
        // 将产生的jwt令牌放入响应头，返回给前端
        response.setHeader("token",token);
        kaSecurityLoginLogService.save(new KaSecurityLoginLog(null, IPUtils.getIpAddr(request),user.getUserName(),null));
        return this.getKaSecurityUserVO(user);
    }

    @Override
    public KaSecurityLoginUserVO getLoginKaSecurityUserVO(KaSecurityUser user) {
        if (user == null) {
            return null;
        }
        KaSecurityLoginUserVO loginUserVO = new KaSecurityLoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);
        return loginUserVO;
    }

    @Override
    public KaSecurityUserVO getKaSecurityUserVO(KaSecurityUser kaSecurityUser) {
        if (kaSecurityUser == null) {
            return null;
        }
        KaSecurityUserVO loginUserVO = new KaSecurityUserVO();
        BeanUtils.copyProperties(kaSecurityUser, loginUserVO);
        return loginUserVO;
    }

    @Override
    public List<KaSecurityUserVO> getKaSecurityUserVO(List<KaSecurityUser> userList) {
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getKaSecurityUserVO).collect(Collectors.toList());
    }

    @Override
    public QueryWrapper<KaSecurityUser> getQueryWrapper(KaSecurityUserQueryRequest kaSecurityUserQueryRequest) {
        if (kaSecurityUserQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Integer id = kaSecurityUserQueryRequest.getId();
//        String mpOpenId = kaSecurityUserQueryRequest.getMpOpenId();
        String userName = kaSecurityUserQueryRequest.getUserName();
        String userRole = kaSecurityUserQueryRequest.getUserRole();
        String sortField = kaSecurityUserQueryRequest.getSortField();
        String sortOrder = kaSecurityUserQueryRequest.getSortOrder();
        if (StringUtils.isEmpty(sortOrder)){
            sortOrder = KaSecurityConstant.SORT_ORDER_ASC;
        }
        QueryWrapper<KaSecurityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
//        queryWrapper.eq(StringUtils.isNotBlank(unionId), "unionId", unionId);
//        queryWrapper.eq(StringUtils.isNotBlank(mpOpenId), "mpOpenId", mpOpenId);
        queryWrapper.eq(StringUtils.isNotBlank(userRole), "user_role", userRole);
        queryWrapper.like(StringUtils.isNotBlank(userName), "user_name", userName);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(KaSecurityConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public KaSecurityUser querryGetKaSecurityUser(KaSecurityUser kaSecurityUser) {
        QueryWrapper<KaSecurityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",kaSecurityUser.getUserName());
        KaSecurityUser one = this.getOne(queryWrapper);
        return one;
    }

    @Override
    public List<IncGraphNode> getGraphIncData(Integer num, DateUnit dateUnit) {
        List<IncGraphNode> allByCreateTimeIncGraphs = this.baseMapper.getAllByCreateTimeIncGraphs(num, dateUnit.getValue());
        return allByCreateTimeIncGraphs;
    }

    @Override
    public String getEntryPassWord(String kaSecurityUserPassWord) {
        return DigestUtils.md5DigestAsHex((SALT + kaSecurityUserPassWord).getBytes());
    }
}




