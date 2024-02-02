package cn.katool.security.starter.utils;

import cn.katool.security.core.config.KaSecurityCoreConfig;
import cn.katool.security.core.constant.KaSecurityConstant;
import cn.katool.security.starter.gateway.gateway.utils.RequestContextUtil;
import cn.katool.util.auth.AuthUtil;
import cn.katool.util.classes.SpringContextUtils;
import cn.katool.util.database.nosql.RedisUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;


public class KaSecurityAuthUtil<T> implements DefaultKaSecurityAuthUtilInterface<T>{
    @Override
    public T getPayLoadWithHeader() {
        return DefaultKaSecurityAuthUtilInterface.super.getPayLoadWithHeader();
    }

    @Override
    public T getPayLoadWithDubboRPC() {
        return DefaultKaSecurityAuthUtilInterface.super.getPayLoadWithDubboRPC();
    }

    @Override
    public T getPayLoad() {
        return DefaultKaSecurityAuthUtilInterface.super.getPayLoad();
    }

    @Override
    public String getTokenWithDubboRPC() {
        return DefaultKaSecurityAuthUtilInterface.super.getTokenWithDubboRPC();
    }

    @Override
    public String getTokenWithHeader() {
        return DefaultKaSecurityAuthUtilInterface.super.getTokenWithHeader();
    }

    @Override
    public String getTokenWithHeader(String headerName){
        return RequestContextUtil.getRequestMono().flatMap(
                request -> Mono.just(request.getHeaders().getFirst(headerName))
        ).block();
    }
    @Override
    public String getTokenWithParameter(String parameterName){
        return RequestContextUtil.getRequestMono().flatMap(
                request -> Mono.just(request.getQueryParams().getFirst(parameterName))
        ).block();
    }
    @Override
    public String getTokenWithCookie(String cookieName){
        return RequestContextUtil.getRequestMono().flatMap(
                request -> Mono.just(request.getCookies().getFirst(cookieName).getValue())
        ).block();
    }
    @Override
    public String getTokenWithHeaderOrParameter(String headerName,String parameterName){
        return getTokenWithHeader(headerName)==null?getTokenWithParameter(parameterName):getTokenWithHeader(headerName);
    }

    public String login(T payload){
        // 生成Token
        String token = AuthUtil.createToken(payload);
        ServerHttpResponse response = RequestContextUtil.getResponse();
        response.getHeaders().add(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER,token);
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN,token,KaSecurityConstant.USER_ONLINE);
        return token;
    }

    public Boolean logout(){
        // 生成Token
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
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN,token,KaSecurityConstant.USER_OFFLINE);
        return true;
    }
}
