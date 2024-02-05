package cn.katool.security.starter.utils;
import cn.katool.security.core.model.entity.TokenStatus;
import cn.katool.security.core.model.entity.UserAgentInfo;
import eu.bitwalker.useragentutils.*;

import cn.katool.security.core.config.KaSecurityCoreConfig;
import cn.katool.security.core.constant.KaSecurityConstant;
import cn.katool.util.auth.AuthUtil;
import cn.katool.util.classes.SpringContextUtils;
import cn.katool.util.database.nosql.RedisUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AbstractKaSecurityAuthUtil<T> extends  DefaultKaSecurityAuthUtilInterface<T> {
    @Override
    default T getPayLoadWithHeader() {
        return DefaultKaSecurityAuthUtilInterface.super.getPayLoadWithHeader();
    }

    @Override
    default T getPayLoadWithDubboRPC() {
        return DefaultKaSecurityAuthUtilInterface.super.getPayLoadWithDubboRPC();
    }

    @Override
    default T getPayLoad() {
        return DefaultKaSecurityAuthUtilInterface.super.getPayLoad();
    }

    @Override
    default String getTokenWithDubboRPC() {
        return DefaultKaSecurityAuthUtilInterface.super.getTokenWithDubboRPC();
    }

    default HttpServletRequest getRequest(){
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return request;
    }

    default HttpServletResponse getResponse(){
        HttpServletResponse response=((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
        return response;
    }

    @Override
    default String login(T payload){
        // 生成Token
        String token = AuthUtil.createToken(payload);
        HttpServletResponse response = getResponse();
        response.setHeader(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER, token);
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        String payLoadPrimary = getPayLoadPrimary(payload);
        TokenStatus tokenStatus = new TokenStatus(payLoadPrimary, getUserAgent(), KaSecurityConstant.USER_ONLINE);
        boolean res = redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token, tokenStatus) &
                redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + payLoadPrimary,token, tokenStatus);
        return res?token:null;
    }
    @Override
    default String login(T payload, Class clazz){
        // 生成Token
        String token = AuthUtil.createToken(payload);
        HttpServletResponse response = getResponse();
        response.setHeader(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER, token);
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        TokenStatus tokenStatus = new TokenStatus(getPayLoadPrimary(token,clazz), getUserAgent(), KaSecurityConstant.USER_ONLINE);
        boolean res = redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token, tokenStatus
        ) & redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + getPayLoadPrimary(token,clazz),token, tokenStatus);
        return res?token:null;
    }

    
    @Override
    default UserAgentInfo getUserAgent(){
        UserAgent userAgent = UserAgent.parseUserAgentString(getRequest().getHeader("User-Agent"));
        UserAgentInfo convert = UserAgentInfo.convert(userAgent);
        return  convert;
    }
}
