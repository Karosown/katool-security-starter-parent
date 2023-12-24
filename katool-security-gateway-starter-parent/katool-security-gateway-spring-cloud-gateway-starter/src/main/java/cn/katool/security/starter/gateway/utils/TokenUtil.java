package cn.katool.security.starter.gateway.utils;

import cn.katool.security.starter.gatway.utils.TokenUtilInterface;
import reactor.core.publisher.Mono;


public class TokenUtil implements TokenUtilInterface {

    @Override
    public String getTokenWithHeader(String headerName){
        return RequestContextUtil.getRequest().flatMap(
                request -> Mono.just(request.getHeaders().getFirst(headerName))
        ).block();
    }
    @Override
    public String getTokenWithParameter(String parameterName){
        return RequestContextUtil.getRequest().flatMap(
                request -> Mono.just(request.getQueryParams().getFirst(parameterName))
        ).block();
    }
    @Override
    public String getTokenWithCookie(String cookieName){
        return RequestContextUtil.getRequest().flatMap(
                request -> Mono.just(request.getCookies().getFirst(cookieName).getValue())
        ).block();
    }
    @Override
    public String getTokenWithHeaderOrParameter(String headerName,String parameterName){
        return getTokenWithHeader(headerName)==null?getTokenWithParameter(parameterName):getTokenWithHeader(headerName);
    }
}
