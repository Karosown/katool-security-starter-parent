/**
 * Title
 *
 * @ClassName: KaSecurityUserInfoServiceImpl
 * @Description:
 * @author: Karos
 * @date: 2023/5/29 17:48
 * @Blog: https://www.wzl1.top/
 */

package cn.katool.security.auth.service.impl;

import cn.katool.security.auth.constant.KaSecurityUserConstant;
import cn.katool.security.auth.exception.BusinessException;
import cn.katool.security.auth.exception.ErrorCode;
import cn.katool.security.auth.model.entity.KaSecurityUser;
import cn.katool.security.auth.service.KaSecurityUserInfoService;
import cn.katool.security.starter.utils.KaSecurityAuthUtil;
import cn.katool.util.auth.AuthUtil;
import cn.katool.util.database.nosql.RedisUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;


// 别放到 thinktank-user 中去，这是两个不同的服务，认证中心的userinfo未来可以对接第三方登录
@DubboService
public class KaSecurityUserInfoServiceImpl extends KaSecurityAuthUtil<KaSecurityUser> implements KaSecurityUserInfoService {
    /**
     * 获取当前登录用户
     *
     * @param token
     * @return
     */
    @Override
    public KaSecurityUser getLoginUser(String token) {
        // 先判断是否已登录
        // session-cookie模式从session中拿
//        Object userObj = token.getSession().getAttribute(KaSecurityUserConstant.USER_LOGIN_STATE);
        //校验登录状态
        Object userObj= AuthUtil.getPayLoadFromToken(token);
        KaSecurityUser currentPayLoad = (KaSecurityUser) userObj;
        if (currentPayLoad == null || currentPayLoad.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentPayLoad;
    }

    /**
     * 获取当前登录用户（允许未登录）
     *
     * @param token
     * @return
     */
    @Override
    public KaSecurityUser getLoginKaSecurityUserPermitNull(String token) {
        // 先判断是否已登录
        // session-cookie模式从session中拿
//        Object userObj = token.getSession().getAttribute(KaSecurityUserConstant.USER_LOGIN_STATE);
        // jwt模式从token里面去取
        Object userObj= AuthUtil.getPayLoadFromToken(token);
        KaSecurityUser currentPayLoad = (KaSecurityUser) userObj;
        if (currentPayLoad == null || currentPayLoad.getId() == null) {
            return null;
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        return currentPayLoad;
    }

    /**
     * 是否为管理员
     *
     * @param token
     * @return
     */
    @Override
    public boolean isAdmin(String token) {
        // 仅管理员可查询
        // session-cookie模式从session中拿
//        Object userObj = token.getSession().getAttribute(KaSecurityUserConstant.USER_LOGIN_STATE);
        // jwt模式从token里面去取
//        String token = token.getHeader(CommonConstant.TOKEN_HEADER);
//        String tokenStatus = (String) redisUtils.getValue("KaSecurityUser:Login" + token);
//        if (Integer.parseInt(tokenStatus) >0){
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
        Object userObj= AuthUtil.getPayLoadFromToken(token);
        KaSecurityUser currentPayLoad = (KaSecurityUser) userObj;
        KaSecurityUser user = (KaSecurityUser) userObj;
        return isAdmin(user);
    }

    @Override
    public boolean isAdmin(KaSecurityUser user) {
        return user != null && KaSecurityUserConstant.ADMIN_ROLE.equals(user.getUserRole());
    }

    @Resource
    RedisUtils redisUtils;


    @Override
    public KaSecurityUser getLoginKaSecurityUser(String token) {
        // 先判断是否已登录
        // session-cookie模式从session中拿
//        Object userObj = token.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        //校验登录状态
        Object userObj= AuthUtil.getPayLoadFromToken(token);
        KaSecurityUser currentUser = (KaSecurityUser) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }
}
