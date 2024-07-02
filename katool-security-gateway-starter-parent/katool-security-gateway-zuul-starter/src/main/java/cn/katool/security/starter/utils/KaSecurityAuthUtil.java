package cn.katool.security.starter.utils;

import com.netflix.zuul.context.RequestContext;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component("KaSecurityAuthUtil")
public class KaSecurityAuthUtil<T> implements AbstractKaSecurityAuthUtil<T>{
    @Override
    public HttpServletResponse getResponse() {
        return RequestContext.getCurrentContext().getResponse();
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
        return RequestContext.getCurrentContext().getRequest();
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
        return getTokenWithHeader(headerName) == null ? getTokenWithParameter(parameterName) : getTokenWithHeader(headerName);
    }

}
