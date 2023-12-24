package cn.katool.security.starter.gatway.utils;

import reactor.core.publisher.Mono;


public interface TokenUtilInterface {

    public  String getTokenWithHeader(String headerName);
    public  String getTokenWithParameter(String parameterName);
    public  String getTokenWithCookie(String cookieName);
    public  String getTokenWithHeaderOrParameter(String headerName,String parameterName);
}
