package cn.katool.security.starter.utils;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("KaSecurityAuthUtil")
public class KaSecurityAuthUtil<T> implements AbstractKaSecurityAuthUtil<T>{
    @Override
    public HttpServletResponse getResponse() {
        return AbstractKaSecurityAuthUtil.super.getResponse();
    }

    @Override
    public String login(T payload) {
        return AbstractKaSecurityAuthUtil.super.login(payload);
    }

    @Override
    public String login(T payload, Class clazz) {
        return AbstractKaSecurityAuthUtil.super.login(payload,clazz);
    }

    @Override
    public T getPayLoadWithHeader() {
        return AbstractKaSecurityAuthUtil.super.getPayLoadWithHeader();
    }

    @Override
    public T getPayLoadWithDubboRPC() {
        return AbstractKaSecurityAuthUtil.super.getPayLoadWithDubboRPC();
    }

    @Override
    public T getPayLoad() {
        return AbstractKaSecurityAuthUtil.super.getPayLoad();
    }

    @Override
    public String getTokenWithDubboRPC() {
        return AbstractKaSecurityAuthUtil.super.getTokenWithDubboRPC();
    }

    @Override
    public HttpServletRequest getRequest() {
        return AbstractKaSecurityAuthUtil.super.getRequest();
    }

    @Override
    public String getTokenWithHeader(String headerName) {
        String token = getRequest().getHeader(headerName);
        return token.substring(token.indexOf("Bearer ")+1);
    }

    @Override
    public String getTokenWithParameter(String parameterName) {
        String token = getRequest().getParameter(parameterName);
        return token.substring(token.indexOf("Bearer ")+1);
    }

    @Override
    public String getTokenWithCookie(String cookieName) {
        HttpServletRequest request = getRequest();
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                String token = cookie.getValue();
                return token.substring(token.indexOf("Bearer ")+1);
            }
        }
        return null;
    }

    @Override
    public String getTokenWithHeaderOrParameter(String headerName, String parameterName) {
        String token = getTokenWithHeader(headerName) == null ? getTokenWithParameter(parameterName) : getTokenWithHeader(headerName);
        return token.substring(token.indexOf("Bearer ")+1);
    }
}
