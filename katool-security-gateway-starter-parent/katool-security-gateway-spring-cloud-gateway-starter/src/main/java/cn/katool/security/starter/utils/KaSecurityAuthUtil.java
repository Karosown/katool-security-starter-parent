package cn.katool.security.starter.utils;

import cn.katool.security.starter.gateway.gateway.utils.RequestContextUtil;
import reactor.core.publisher.Mono;


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
