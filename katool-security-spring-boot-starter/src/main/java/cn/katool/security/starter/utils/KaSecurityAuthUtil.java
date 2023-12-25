package cn.katool.security.starter.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class KaSecurityAuthUtil<T> implements AbstractKaSecurityAuthUtil<T>{

    @Override
    public HttpServletRequest getRequest() {
        return AbstractKaSecurityAuthUtil.super.getRequest();
    }

    @Override
    public String getTokenWithHeader(String headerName) {
        return getRequest().getHeader(headerName);
    }

    @Override
    public String getTokenWithParameter(String parameterName) {
        return getRequest().getParameter(parameterName);
    }

    @Override
    public String getTokenWithCookie(String cookieName) {
        HttpServletRequest request = getRequest();
        for (Cookie cookie : request.getCookies()) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    @Override
    public String getTokenWithHeaderOrParameter(String headerName, String parameterName) {
        return getTokenWithHeader(headerName) == null ? getTokenWithParameter(parameterName) : getTokenWithHeader(headerName);
    }
}
