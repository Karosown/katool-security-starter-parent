package cn.katool.security.starter.utils;

import cn.katool.security.core.annotation.AuthPrimary;
import cn.katool.security.core.config.KaSecurityCoreConfig;

import cn.katool.security.core.constant.KaSecurityConstant;
import cn.katool.security.core.constant.KaSecurityMode;
import cn.katool.security.core.model.entity.TokenStatus;
import cn.katool.security.core.model.entity.UserAgentInfo;
import cn.katool.util.auth.AuthUtil;
import cn.katool.util.classes.SpringContextUtils;
import cn.katool.util.database.nosql.RedisUtils;
import com.qiniu.util.Auth;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.yaml.snakeyaml.introspector.PropertySubstitute;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public interface DefaultKaSecurityAuthUtilInterface<T> {

    default String getPayLoadPrimary(){
        T payLoad = getPayLoad();
        Field[] declaredFields = payLoad.getClass().getDeclaredFields();
        Object primary = null;
        for (Field field : declaredFields) {
            boolean annotationPresent = field.isAnnotationPresent(AuthPrimary.class);
            if (annotationPresent){
                field.setAccessible(true);
                try {
                    primary = field.get(payLoad);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return primary.toString();
    }

    default String getPayLoadPrimary(T payLoad){
        Field[] declaredFields = payLoad.getClass().getDeclaredFields();
        Object primary = null;
        for (Field field : declaredFields) {
            boolean annotationPresent = field.isAnnotationPresent(AuthPrimary.class);
            if (annotationPresent){
                field.setAccessible(true);
                try {
                    primary = field.get(payLoad);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return primary.toString();
    }

    default String getPayLoadPrimaryWithToken(String token){
        T payLoad = AuthUtil.getPayLoadFromToken(token);
        Field[] declaredFields = payLoad.getClass().getDeclaredFields();
        Object primary = null;
        for (Field field : declaredFields) {
            boolean annotationPresent = field.isAnnotationPresent(AuthPrimary.class);
            if (annotationPresent){
                field.setAccessible(true);
                try {
                    primary = field.get(payLoad);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return primary.toString();
    }

    default T getPayLoadWithHeader(){
        return (T) AuthUtil.getPayLoadFromToken(getTokenWithHeader(),
                (Class)((ParameterizedType)
                        getClass().getGenericSuperclass()).getActualTypeArguments()[0]
                );
    }

    default T getPayLoadWithDubboRPC(){
        String token = RpcContext.getContext().getAttachment(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
        return AuthUtil.getPayLoadFromToken(token);
    }
    default T getPayLoad(){
        T payLoad = getPayLoadWithHeader();
        if (ObjectUtils.isEmpty(payLoad)){
            payLoad = getPayLoadWithDubboRPC();
        }
        if (ObjectUtils.isEmpty(payLoad)){
            payLoad = getPayLoadWithDubboRPC();
        }
        return payLoad;
    }

    default String getTokenWithDubboRPC(){
        return RpcContext.getContext().getAttachment(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
    }

    default String getTokenWithHeader(){
        return getTokenWithHeader(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
    }

    String getTokenWithHeader(String headerName);
    String getTokenWithParameter(String parameterName);
    String getTokenWithCookie(String cookieName);
    default String getTokenWithHeaderOrParameter(String headerName, String parameterName){
        String tokenWithHeader = getTokenWithHeader(headerName);
        return tokenWithHeader ==null?getTokenWithParameter(parameterName): tokenWithHeader;
    }

    default String getTokenAllIn(String name){
        String token = getTokenWithHeaderOrParameter(name, name);
        token = null == token?getTokenWithCookie(name):token;
        token = null == token?getTokenWithDubboRPC():token;
        return token;
    }
    default String getTokenAllInDefineHeader(){
        return getTokenAllIn(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
    }
    UserAgentInfo getUserAgent();

    default List<Map.Entry<String,TokenStatus>> getTokenStatusAllList(){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        Map map = redisUtils.getMap(KaSecurityConstant.CACHE_LOGIN_TOKEN);
        List<Map.Entry<String,TokenStatus>> tokenList = (List<Map.Entry<String,TokenStatus>>) map.entrySet().stream().collect(Collectors.toList());
        return tokenList;
    }
    default List<Map.Entry<String,TokenStatus>> getTokenStatusListWithCurrentPayLoad(){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        String payLoadPrimary = getPayLoadPrimary();
        Map map = redisUtils.getMap(KaSecurityConstant.CACHE_LOGIN_TOKEN+":"+payLoadPrimary);
        List<Map.Entry<String,TokenStatus>> tokenList = (List<Map.Entry<String,TokenStatus>>) map.entrySet().stream().collect(Collectors.toList());
        return tokenList;
    }

    default TokenStatus getCurrentTokenStatus(){
        String token = getTokenAllInDefineHeader();
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        TokenStatus tokenStatus = (TokenStatus) redisUtils.getMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
        return tokenStatus;
    }

    default Boolean isLogin(){
        T payLoad = this.getPayLoad();
        if (null == payLoad){
            return false;
        }
        TokenStatus currentTokenStatus = getCurrentTokenStatus();
        if (null == currentTokenStatus){
            return false;
        }
        if (KaSecurityConstant.USER_OFFLINE.equals(currentTokenStatus.getStatus())){
            return false;
        }
        return true;
    }

    String login(T payload);

    default Boolean login(String token, TokenStatus tokenStatus){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        boolean res = redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token, tokenStatus) &
                redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + tokenStatus.getPrimary(), token, tokenStatus);
        return res;
    }
    default Boolean login(String primary,String token){
        return login(token,new TokenStatus(primary,getUserAgent(),KaSecurityConstant.USER_ONLINE));
    }

    default Boolean logout(String primary,String token){
        return delToken(primary,token);
    }

    default Boolean delToken(String primary, String token){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        Boolean isDel = redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + primary, token) &
                redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);;
        return isDel;
    }

    default Boolean logout(){
        String token = getTokenWithHeader(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
        if ((null) == token || "".equals(token)){
            token = getTokenWithParameter(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
            if ((null) == token || "".equals(token)){
                token = getTokenWithHeader(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
                if ((null) == token || "".equals(token)){
                    return false;
                }
            }
        }
        return delToken(token);
    }

    default Boolean kickout(String token){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        TokenStatus tokenStatus = (TokenStatus) redisUtils.getMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
        tokenStatus.setStatus(KaSecurityConstant.USER_OFFLINE);
        String primary=getPayLoadPrimary();
        Boolean isLogout = redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN+":"+primary, token, tokenStatus)
                && redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token,tokenStatus);
        return isLogout;
    }

    default Boolean kickout(String parimary,String token){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        TokenStatus tokenStatus = (TokenStatus) redisUtils.getMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
        tokenStatus.setStatus(KaSecurityConstant.USER_OFFLINE);
        Boolean isKickOut = redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN+":"+parimary, token, tokenStatus) &
                                            redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token,tokenStatus);
        return isKickOut;
    }

    default Boolean delToken(String token){
        // 获取该token的用户主要标志
        String primary = getPayLoadPrimaryWithToken(token);
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        Boolean isDel = redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + primary, token) &
                redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);;
        return isDel;
    }

    default Boolean expireToken(String token){
        if (!AuthUtil.verifyToken(token)){
            return false;
        }
        if (!AuthUtil.isExpired(token)){
            return false;
        }
        String tokenPrimary = getPayLoadPrimaryWithToken(token);
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        boolean isExipire = redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + tokenPrimary, token) &
                redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
        return isExipire;
    }

    default void doCleanExpireToken(Integer effort){
        List<Map.Entry<String, TokenStatus>> tokenStatusAllList = getTokenStatusAllList();
        int size = tokenStatusAllList.size();
        if (size == 0){
            return;
        }
        int count = effort * 100 / size;
        int cnt = 0;
        for (Map.Entry<String, TokenStatus> v : tokenStatusAllList) {
            if (cnt >= count){
                return;
            }
            String token = v.getKey();
            if (expireToken(token)) {
                if (++cnt  >= count ){
                    return ;
                }
            }
        }
    }
    default void doCleanExpireToken(){
        doCleanExpireToken(80);
    }
}
