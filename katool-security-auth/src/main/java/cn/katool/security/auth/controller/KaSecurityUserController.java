package cn.katool.security.auth.controller;

import cn.hutool.crypto.digest.DigestUtil;
import cn.katool.security.auth.constant.KaSecurityUserConstant;
import cn.katool.security.auth.exception.BusinessException;
import cn.katool.security.auth.exception.ErrorCode;
import cn.katool.security.auth.model.dto.*;
import cn.katool.security.auth.model.dto.user.*;
import cn.katool.security.auth.model.entity.KaSecurityUser;
import cn.katool.security.auth.model.vo.kauser.KaSecurityLoginUserVO;
import cn.katool.security.auth.model.vo.kauser.KaSecurityUserVO;
import cn.katool.security.auth.service.KaSecurityUserInfoService;
import cn.katool.security.auth.service.KaSecurityUserService;
import cn.katool.security.auth.service.impl.KaSecurityUserServiceImpl;
import cn.katool.security.auth.utils.AesUtils;
import cn.katool.security.auth.utils.BaseResponse;
import cn.katool.security.auth.utils.ResultUtils;
import cn.katool.security.auth.utils.ThrowUtils;
import cn.katool.security.core.annotation.AuthCheck;
import cn.katool.security.core.utils.JSONUtils;
import cn.katool.security.starter.utils.KaSecurityAuthUtil;
import cn.katool.util.auth.AuthUtil;
import cn.katool.util.database.nosql.RedisUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@Slf4j
@Tag(name = "中间件用户管理")
@RestController
@RequestMapping("/api/user")
public class KaSecurityUserController extends KaSecurityAuthUtil<KaSecurityUser> {

    @Resource
    KaSecurityUserService kaSecurityUserService;

    @Resource
    KaSecurityUserInfoService kaSecurityUserInfoService;
    @Resource
    RedisUtils redisUtils;
    @Operation(summary = "公钥获取接口",description = "公钥获取接口")
    @GetMapping("/pbs")
    public BaseResponse<String> pbs(){
        String publicKey = kaSecurityUserService.generateKeyPairAndReturnPublicKey();
        return ResultUtils.success(publicKey);
    }

    @Operation(summary = "UUID获取接口",description = "UUID获取接口")
    @PostMapping("/uuid")
    public BaseResponse<String> scu(@RequestBody SecretPair secretPair) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        String pub = secretPair.getPub();
        String hexMsg = secretPair.getHexMsg();
        String uuid = kaSecurityUserService.getUUID(pub, hexMsg);
        return ResultUtils.success(uuid);
    }



    /**
     * 用户登录
     *
     * @param doLoginRequest
     * @param request
     * @return
     */
    @Operation(summary = "用户登录",description = "用户登录接口")
    @PostMapping("/login")
    public BaseResponse<KaSecurityUserVO> doLogin(@RequestBody KaSecurityUserLoginRequest doLoginRequest, HttpServletRequest request, HttpServletResponse response) {
        //uuid解密步骤
        String uuid = kaSecurityUserService.getUUID(request);
//        doLoginRequest.getPassWord();
//        String uuidDecrypt = AesUtils.decrypt((String) passWord,(String) request.getSession().getAttribute("uuid"));
        //解密如果为null返回系统异常
        if (StringUtils.isAnyBlank(uuid)){
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR,"页面已过期，请刷新后重新访问登录");
        }
        if (doLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String kaSecurityUserName = doLoginRequest.getUserName();
        String kaSecurityUserPassWord=doLoginRequest.getPassWord();
        if (StringUtils.isAnyBlank(kaSecurityUserName, kaSecurityUserPassWord)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        log.info("kaSecurityUserName={} kaSecurityUserPassWord={} uuid={}",kaSecurityUserName,kaSecurityUserPassWord,uuid);
        kaSecurityUserPassWord = kaSecurityUserService.decrypt(kaSecurityUserPassWord, uuid);
        log.info("kaSecurityUserName={} kaSecurityUserPassWord={}",kaSecurityUserName,kaSecurityUserPassWord);
        //正常登录
        KaSecurityUserVO kaSecurityUserVO = kaSecurityUserService.doLogin(kaSecurityUserName, kaSecurityUserPassWord, request,response);
        return ResultUtils.success(kaSecurityUserVO);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request) {
        return ResultUtils.success(kaSecurityUserService.userLogout(this.getTokenAllInDefineHeader()));
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Operation(summary = "获取当前登录用户",description = "获取当前登录用户接口")
    @GetMapping("/get/usinfo")
    public BaseResponse<KaSecurityLoginUserVO> getLoginKaSecurityUser(HttpServletRequest request) {
        KaSecurityUser kaSecurityUser = kaSecurityUserInfoService.getLoginKaSecurityUserPermitNull(AuthUtil.getToken(request));
        if (ObjectUtils.isEmpty(kaSecurityUser)){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return ResultUtils.success(kaSecurityUserService.getLoginKaSecurityUserVO(kaSecurityUser));
    }

    // endregion

    // region 增删改查

    /**
     * 创建用户
     *
     * @param kaSecurityUserAddRequest
     * @param request
     * @return
     */
    @Operation(summary = "新增用户（管理员权限）",description = "新增用户接口（管理员权限）")
    @PostMapping("/add")
    @AuthCheck(mustRole = KaSecurityUserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> addKaSecurityUser(@RequestBody KaSecurityUserAddRequest kaSecurityUserAddRequest, HttpServletRequest request) {
        if (kaSecurityUserAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        KaSecurityUser kaSecurityUser = new KaSecurityUser();
        BeanUtils.copyProperties(kaSecurityUserAddRequest, kaSecurityUser);
        String kaSecurityUserPassWord = kaSecurityUser.getPassWord();
        String uuid = kaSecurityUserService.getUUID(request);
        log.info("entry:{} uuid:{}",kaSecurityUserPassWord,uuid);
        kaSecurityUserPassWord = AesUtils.decrypt(kaSecurityUserPassWord, uuid);
        kaSecurityUser.setPassWord(DigestUtil.md5Hex(KaSecurityUserServiceImpl.SALT+ kaSecurityUserPassWord));
        boolean result = kaSecurityUserService.save(kaSecurityUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(kaSecurityUser.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @Operation(summary = "删除用户（管理员权限）",description = "删除用户接口 -- 管理员")
    @PostMapping("/delete")
    @AuthCheck(mustRole = KaSecurityUserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteKaSecurityUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (ObjectUtils.isEmpty(deleteRequest) ||
                deleteRequest.getId()<=0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = kaSecurityUserService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param kaSecurityUserUpdateRequest
     * @param request
     * @return
     */
    @Operation(summary = "用户信息更新（管理员权限）",description = "用户信息更新接口 —— 管理员")
    @PostMapping("/update")
    @AuthCheck(mustRole = KaSecurityUserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateKaSecurityUser(@RequestBody KaSecurityUserUpdateRequest kaSecurityUserUpdateRequest,
                                            HttpServletRequest request) {
        if (kaSecurityUserUpdateRequest == null || kaSecurityUserUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String kaSecurityUserPassWord = kaSecurityUserUpdateRequest.getPassWord();
        if (StringUtils.isNotEmpty(kaSecurityUserPassWord)){
            kaSecurityUserPassWord = AesUtils.decrypt(kaSecurityUserPassWord, request.getHeader("uuid"));
            String entryPassWord= kaSecurityUserService.getEntryPassWord(kaSecurityUserPassWord);

            kaSecurityUserUpdateRequest.setPassWord(entryPassWord);
        }
        KaSecurityUser kaSecurityUser = new KaSecurityUser();
        BeanUtils.copyProperties(kaSecurityUserUpdateRequest, kaSecurityUser);
        if (ObjectUtils.isEmpty(kaSecurityUser.getId())){
            KaSecurityUser one = kaSecurityUserService.querryGetKaSecurityUser(kaSecurityUser);
            kaSecurityUser.setId(one.getId());
        }
        boolean result = kaSecurityUserService.updateById(kaSecurityUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR,"修改失败，请检查参数是否未修改或者修改有误！");
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取用户（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @Operation(summary = "用户全信息获取（管理员）",description = "用户全信息获取接口（通过ID） —— 管理员")
    @GetMapping("/get")
    @AuthCheck(mustRole = KaSecurityUserConstant.ADMIN_ROLE)
    public BaseResponse<KaSecurityUser> getById(String id, HttpServletRequest request) {
        if (StringUtils.isEmpty(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        KaSecurityUser kaSecurityUser = kaSecurityUserService.getById(id);
        ThrowUtils.throwIf(kaSecurityUser == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(kaSecurityUser);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param kaSecurityUserName
     * @param request
     * @return
     */
    @Operation(summary = "用户信息获取",description = "用户VO信息获取接口")
    @GetMapping("/get/vo/{kaSecurityUserName}")
    public BaseResponse<KaSecurityLoginUserVO> getVOByUserName(@PathVariable("kaSecurityUserName") String kaSecurityUserName, HttpServletRequest request) {
        QueryWrapper<KaSecurityUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name",kaSecurityUserName);
        KaSecurityUser kaSecurityUser = kaSecurityUserService.getOne(queryWrapper);
        if (ObjectUtils.isEmpty(kaSecurityUser)){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(kaSecurityUserService.getLoginKaSecurityUserVO(kaSecurityUser));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param kaSecurityUserQueryRequest
     * @param request
     * @return
     */
    @Operation(summary = "用户信息分页查询（管理员）",description = "用户信息分页获取搜索接口 —— 管理员")
    @PostMapping("/list/page")
    @AuthCheck(mustRole = KaSecurityUserConstant.ADMIN_ROLE)
    public BaseResponse<Page<KaSecurityUser>> listKaSecurityUserByPage(@RequestBody KaSecurityUserQueryRequest kaSecurityUserQueryRequest,
                                                   HttpServletRequest request) {
        long current = kaSecurityUserQueryRequest.getCurrent();
        long size = kaSecurityUserQueryRequest.getPageSize();
        Page<KaSecurityUser> kaSecurityUserPage = kaSecurityUserService.page(new Page<>(current, size),
                kaSecurityUserService.getQueryWrapper(kaSecurityUserQueryRequest));
        return ResultUtils.success(kaSecurityUserPage);
    }

    /**
     * 分页获取用户封装列表
     *
     * @param kaSecurityUserQueryRequest
     * @param request
     * @return
     */
    @Operation(summary = "用户信息分页查询（VO）",description = "用户VO信息分页获取搜索接口")
    @PostMapping("/list/page/vo")
    @AuthCheck(anyRole = KaSecurityUserConstant.ADMIN_ROLE)
    public BaseResponse<Page<KaSecurityUserVO>> listKaSecurityUserVOByPage(@RequestBody KaSecurityUserQueryRequest kaSecurityUserQueryRequest,
                                                       HttpServletRequest request) {
        if (kaSecurityUserQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = kaSecurityUserQueryRequest.getCurrent();
        long size = kaSecurityUserQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<KaSecurityUser> kaSecurityUserPage = kaSecurityUserService.page(new Page<>(current, size),
                kaSecurityUserService.getQueryWrapper(kaSecurityUserQueryRequest));
        Page<KaSecurityUserVO> kaSecurityUserVOPage = new Page<>(current, size, kaSecurityUserPage.getTotal());
        List<KaSecurityUserVO> kaSecurityUserVO = kaSecurityUserService.getKaSecurityUserVO(kaSecurityUserPage.getRecords());
        kaSecurityUserVOPage.setRecords(kaSecurityUserVO);
        return ResultUtils.success(kaSecurityUserVOPage);
    }

    // endregion
    /**
     * 更新个人信息
     *
     * @param kaSecurityUserUpdateMyRequest
     * @param request
     * @return
     */
    @Operation(summary = "个人资料更新",description = "用户信息更新接口 —— 用户")
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyKaSecurityUser(@RequestBody KaSecurityUserUpdateMyRequest kaSecurityUserUpdateMyRequest,
                                              HttpServletRequest request) {
        if (kaSecurityUserUpdateMyRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        KaSecurityUser loginKaSecurityUser = kaSecurityUserInfoService.getLoginKaSecurityUser(AuthUtil.getToken(request));
        KaSecurityUser kaSecurityUser = new KaSecurityUser();
        BeanUtils.copyProperties(kaSecurityUserUpdateMyRequest, kaSecurityUser);
        kaSecurityUser.setId(loginKaSecurityUser.getId());
        boolean result = kaSecurityUserService.updateById(kaSecurityUser);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    @Operation(summary = "密码修改",description = "密码修改接口 - 用户")
    @PostMapping("/update/secret")
    public BaseResponse<String> secret(@RequestBody KaSecurityUserUpdatePassWordRequest kaSecurityUserUpdatePassWordRequest, HttpServletRequest request){
        KaSecurityUser loginKaSecurityUser = kaSecurityUserInfoService.getLoginKaSecurityUser(AuthUtil.getToken(request));
        loginKaSecurityUser.setPassWord(AesUtils.decrypt(kaSecurityUserUpdatePassWordRequest.getPassWord(),kaSecurityUserUpdatePassWordRequest.getUuid()));
        kaSecurityUserService.updateById(loginKaSecurityUser);
        return ResultUtils.success("修改密码成功");
    }

}
