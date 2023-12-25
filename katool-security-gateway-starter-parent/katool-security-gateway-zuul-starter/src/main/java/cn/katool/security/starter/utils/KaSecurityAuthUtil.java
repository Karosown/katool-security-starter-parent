package cn.katool.security.starter.utils;

import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class KaSecurityAuthUtil<T> implements AbstractKaSecurityAuthUtil<T>{
    @Override
    public HttpServletResponse getResponse() {
        return RequestContext.getCurrentContext().getResponse();
    }

    @Override
    public String login(T payload) {
        return AbstractKaSecurityAuthUtil.super.login(payload);
    }

    @Override
    public HttpServletRequest getRequest() {
        return RequestContext.getCurrentContext().getRequest();
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
