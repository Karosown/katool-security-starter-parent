package cn.katool.security.starter.utils;

import cn.katool.security.core.config.KaSecurityCoreConfig;
import cn.katool.security.core.constant.KaSecurityConstant;
import cn.katool.security.core.model.entity.TokenStatus;
import cn.katool.security.core.model.entity.UserAgentInfo;
import cn.katool.security.starter.gateway.gateway.utils.RequestContextUtil;
import cn.katool.util.auth.AuthUtil;
import cn.katool.util.classes.SpringContextUtils;
import cn.katool.util.database.nosql.RedisUtils;
import com.alibaba.excel.util.StringUtils;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletResponse;


@Component("KaSecurityAuthUtil")
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
        String token = RequestContextUtil.getRequestMono().flatMap(
                request -> Mono.just(request.getHeaders().getFirst(headerName))
        ).block();
        if (StringUtils.isNotBlank(token)){
            return token.substring(token.indexOf("Bearer ")+"Bearer ".length());
        }
        return null;
    }
    @Override
    public String getTokenWithParameter(String parameterName){
        String token = RequestContextUtil.getRequestMono().flatMap(
                request -> Mono.just(request.getQueryParams().getFirst(parameterName))
        ).block();
        if (StringUtils.isNotBlank(token)){
            return token.substring(token.indexOf("Bearer ")+"Bearer ".length());
        }
        return null;
    }
    @Override
    public String getTokenWithCookie(String cookieName){
        String token = RequestContextUtil.getRequestMono().flatMap(
                request -> Mono.just(request.getCookies().getFirst(cookieName).getValue())
        ).block();
        if (StringUtils.isNotBlank(token)){
            return token.substring(token.indexOf("Bearer ")+"Bearer ".length());
        }
        return null;
    }

    @Override
    public String login(T payload){
        // 生成Token
        String token = AuthUtil.createToken(payload);
        ServerHttpResponse response = RequestContextUtil.getResponse();
        response.getHeaders().add(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER,token);
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        TokenStatus tokenStatus = new TokenStatus(getPayLoadPrimary(payload), getUserAgent(), KaSecurityConstant.USER_ONLINE);
        Boolean res = (Boolean) redisOper(redisUtils,()-> redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token, tokenStatus) &
                redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + getPayLoadPrimary(payload),token, tokenStatus));
        return res?token:null;
    }
    @Override
    public String login(T payload,Class clazz){
        // 生成Token
        String token = AuthUtil.createToken(payload);
        ServerHttpResponse response = RequestContextUtil.getResponse();
        response.getHeaders().add(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER,token);
        RedisUtils redisUtils = (RedisUtils) SpringContextUtils.getBean("RedisUtils");
        TokenStatus tokenStatus = new TokenStatus(getPayLoadPrimary(token,clazz), getUserAgent(), KaSecurityConstant.USER_ONLINE);
        Boolean res = (Boolean) redisOper(redisUtils,()->redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN, token, tokenStatus) &
                redisUtils.pushMap(KaSecurityConstant.CACHE_LOGIN_TOKEN + ":" + getPayLoadPrimary(token,clazz),token, tokenStatus));
        return res?token:null;
    }




    @Override
    public UserAgentInfo getUserAgent(){
        UserAgent userAgent = UserAgent.parseUserAgentString(RequestContextUtil.getRequest().getHeaders().getFirst("User-Agent"));
        UserAgentInfo convert = UserAgentInfo.convert(userAgent);
        return  convert;
    }
}
