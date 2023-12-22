package cn.katool.security.starter.utils;

import cn.katool.security.gateway.service.TokenUtilInterface;
import reactor.core.publisher.Mono;


import static cn.katool.security.starter.utils.RequestContextUtil.getRequest;


public class TokenUtil implements TokenUtilInterface {

    @Override
    public String getTokenWithHeader(String headerName){
        return getRequest().flatMap(
                request -> Mono.just(request.getHeaders().getFirst(headerName))
        ).block();
    }
    @Override
    public String getTokenWithParameter(String parameterName){
        return getRequest().flatMap(
                request -> Mono.just(request.getQueryParams().getFirst(parameterName))
        ).block();
    }
    @Override
    public String getTokenWithCookie(String cookieName){
        return getRequest().flatMap(
                request -> Mono.just(request.getCookies().getFirst(cookieName).getValue())
        ).block();
    }
    @Override
    public String getTokenWithHeaderOrParameter(String headerName,String parameterName){
        return getTokenWithHeader(headerName)==null?getTokenWithParameter(parameterName):getTokenWithHeader(headerName);
    }
}
