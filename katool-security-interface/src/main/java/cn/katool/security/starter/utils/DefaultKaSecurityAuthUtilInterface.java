package cn.katool.security.starter.utils;

import cn.hutool.json.JSONObject;
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
import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public interface DefaultKaSecurityAuthUtilInterface<T> {

    default Class getPayLoadClass(){

        Type genericSuperclass = this.getClass().getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType){
            Type actualTypeArgument = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[0];
            return (Class<T>) actualTypeArgument;
        }
        try {
            Constructor<?>[] declaredConstructors = Class.forName(genericSuperclass.getTypeName()).getDeclaredConstructors();
            Class<?> aClass = declaredConstructors[0].newInstance().getClass();
            return aClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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

    default T getPayLoad(Class clazz){
        T payLoad = getPayLoadWithHeader(clazz);
        if (ObjectUtils.isEmpty(payLoad)){
            payLoad = getPayLoadWithDubboRPC(clazz);
        }
        return payLoad;
    }

    default String getCurrentPayLoadPrimary(){
        T payLoad = getPayLoad();
        Field[] declaredFields = payLoad.getClass().getDeclaredFields();
        Object primary = null;
        for (Field field : declaredFields) {
            boolean annotationPresent = field.isAnnotationPresent(AuthPrimary.class);
            if (annotationPresent){
                field.setAccessible(true);
                try {
                    primary = field.get(payLoad);
                    break;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (null == primary){
            // 如果没有设置主键，则默认取第一个字段作为主键
            JSONObject payLoad1 = (JSONObject) payLoad;
            primary = payLoad1.get(KaSecurityCoreConfig.defautPrimaryKeyName);
        }
        return primary.toString();
    }
    default String getCurrentPayLoadPrimary(Class clazz){
        T payLoad = getPayLoad(clazz);
        Field[] declaredFields = payLoad.getClass().getDeclaredFields();
        Object primary = null;
        for (Field field : declaredFields) {
            boolean annotationPresent = field.isAnnotationPresent(AuthPrimary.class);
            if (annotationPresent){
                field.setAccessible(true);
                try {
                    primary = field.get(payLoad);
                    break;
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
                    break;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (primary == null){
            JSONObject payLoad1 = (JSONObject) payLoad;
            primary = payLoad1.get(KaSecurityCoreConfig.defautPrimaryKeyName);
        }
        return primary.toString();
    }
    default String getPayLoadPrimary(String token){
        T payLoad = (T) AuthUtil.getPayLoadFromToken(token,getPayLoadClass());
        Field[] declaredFields = payLoad.getClass().getDeclaredFields();
        Object primary = null;
        for (Field field : declaredFields)  {
            boolean annotationPresent = field.isAnnotationPresent(AuthPrimary.class);
            if (annotationPresent){
                field.setAccessible(true);
                try {
                    primary = field.get(payLoad);
                    break;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if (primary == null){
            JSONObject payLoad1 = (JSONObject) payLoad;
            primary = payLoad1.get("id");
        }
        return primary.toString();
    }
    
    default String getPayLoadPrimary(String token,Class clazz){
        T payLoad = (T) AuthUtil.getPayLoadFromToken(token,clazz);
        Field[] declaredFields = payLoad.getClass().getDeclaredFields();
        Object primary = null;
        for (Field field : declaredFields) {
            boolean annotationPresent = field.isAnnotationPresent(AuthPrimary.class);
            if (annotationPresent){
                field.setAccessible(true);
                try {
                    primary = field.get(payLoad);
                    break;
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return primary.toString();
    }

    default T getPayLoadWithHeader(){
        return (T) AuthUtil.getPayLoadFromToken(getTokenWithHeader(),getPayLoadClass());
    }

    default T getPayLoadWithHeader(Class clazz){
        return (T) AuthUtil.getPayLoadFromToken(getTokenWithHeader(),clazz);
    }

    default T getPayLoadWithDubboRPC(){
        String token = RpcContext.getContext().getAttachment(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
        return (T) AuthUtil.getPayLoadFromToken(token,getPayLoadClass());
    }

    default T getPayLoadWithDubboRPC(Class clazz){
        String token = RpcContext.getContext().getAttachment(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
        return (T) AuthUtil.getPayLoadFromToken(token,clazz);
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

    default Object redisOper(RedisUtils redisUtils, Supplier supplier){
        boolean status = false;
        if (redisUtils.getOnfCacheInThread().equals(true)){
            redisUtils.onfCacheInThread(false);
            status = true;
        }
        Object res = supplier.get();
        if (status){
            redisUtils.onfCacheInThread(true);
        }
        return res;
    }

    default List<Map.Entry<String,TokenStatus>> getTokenStatusAllList(){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        List<Map.Entry<String,TokenStatus>> tokenList = (List<Map.Entry<String, TokenStatus>>) redisOper(redisUtils, () -> {
            Map map = redisUtils.getMap(KaSecurityConstant.CACHE_LOGIN_TOKEN);
            return (List<Map.Entry<String, TokenStatus>>) map.entrySet().stream().collect(Collectors.toList());
        });
        return tokenList;
    }
    default List<Map.Entry<String,TokenStatus>> getTokenStatusListWithCurrentPayLoad(Class clazz){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        String payLoadPrimary = getCurrentPayLoadPrimary(clazz);
        return (List<Map.Entry<String, TokenStatus>>) redisOper(redisUtils,()->{
            Map map = redisUtils.getMap(KaSecurityConstant.CACHE_LOGIN_TOKEN+":"+payLoadPrimary);
            return (List<Map.Entry<String,TokenStatus>>) map.entrySet().stream().collect(Collectors.toList());
        });
    }
    default List<Map.Entry<String,TokenStatus>> getTokenStatusListWithCurrentPayLoad(){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        return (List<Map.Entry<String, TokenStatus>>) redisOper(redisUtils,()->{
            String payLoadPrimary = getCurrentPayLoadPrimary();
            Map map = redisUtils.getMap(KaSecurityConstant.CACHE_LOGIN_TOKEN+":"+payLoadPrimary);
            return (List<Map.Entry<String,TokenStatus>>) map.entrySet().stream().collect(Collectors.toList());
        });
    }
    default TokenStatus getCurrentTokenStatus(){
        String token = getTokenAllInDefineHeader();
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        return (TokenStatus) redisOper(redisUtils,()->{
            Object map = redisUtils.getMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
            return map;
        });
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
        return KaSecurityConstant.USER_ONLINE.equals(currentTokenStatus.getStatus());
    }

    String login(T payload);
    String login(T payload,Class clazz);

    default Boolean login(String token, TokenStatus tokenStatus){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");

        return (Boolean) redisOper(redisUtils,()->{
            boolean res = redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token, tokenStatus) &
                    redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + tokenStatus.getPrimary(), token, tokenStatus);
            return res;
        });
    }
    default Boolean login(String primary,String token){
        return login(token,new TokenStatus(primary,getUserAgent(),KaSecurityConstant.USER_ONLINE));
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

    default Boolean logout(String primary,String token){
        return delToken(primary,token);
    }

    default Boolean logout(String token,Class clazz){
            return delToken(token,clazz);
    }

    default Boolean kickout(String token){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        TokenStatus tokenStatus = (TokenStatus) redisUtils.getMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
        tokenStatus.setStatus(KaSecurityConstant.USER_OFFLINE);
        String primary=getPayLoadPrimary(token);
        Boolean isLogout = redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN+":"+primary, token, tokenStatus)
                && redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token,tokenStatus);
        return isLogout;
    }
    default Boolean kickout(String token,Class clazz){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        TokenStatus tokenStatus = (TokenStatus) redisUtils.getMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
        tokenStatus.setStatus(KaSecurityConstant.USER_OFFLINE);
        String primary=getPayLoadPrimary(token);
        Boolean isLogout = redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN+":"+getPayLoadPrimary(token,clazz), token, tokenStatus)
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
        String primary = getPayLoadPrimary(token);
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");

        return (Boolean) redisOper(redisUtils,()->{
            Boolean isDel = redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + primary, token) &
                    redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
            return isDel;
        });
    }

    default Boolean delToken(String primary,String token){
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        return (Boolean) redisOper(redisUtils,()->{
            Boolean isDel = redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + primary, token) &
                    redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
            return isDel;
        });
    }
    default Boolean delToken(String token,Class clazz){
        String primary = getPayLoadPrimary(token,clazz);
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        return (Boolean) redisOper(redisUtils,()->{
            Boolean isDel = redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + primary, token) &
                    redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
            return isDel;
        });
    }

    default Boolean expireToken(String primary,String token){
        if (!AuthUtil.verifyToken(token)){
            return false;
        }
        if (!AuthUtil.isExpired(token)){
            return false;
        }
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        return (Boolean) redisOper(redisUtils,()->{
            Boolean isExpire = redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + primary, token) &
                    redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
            return isExpire;
        });
    }
    default Boolean expireToken(String token){
        if (!AuthUtil.verifyToken(token)){
            return false;
        }
        if (!AuthUtil.isExpired(token)){
            return false;
        }
        String tokenPrimary = getPayLoadPrimary(token);
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        return (Boolean) redisOper(redisUtils,()->{
            Boolean isExpire = redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + tokenPrimary, token) &
                    redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
            return isExpire;
        });
    }

    default Boolean expireToken(String token,Class clazz){
        if (!AuthUtil.verifyToken(token)){
            return false;
        }
        if (!AuthUtil.isExpired(token)){
            return false;
        }
        String tokenPrimary = getPayLoadPrimary(token,clazz);
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        return (Boolean) redisOper(redisUtils,()->{
            Boolean isExpire = redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + tokenPrimary, token) &
                    redisUtils.delMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token);
            return isExpire;
        });
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
            String primary = v.getValue().getPrimary();
            if (expireToken(primary,token)) {
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
