package cn.katool.security.starter.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public interface AbstractKaSecurityAuthUtil<T> extends  DefaultKaSecurityAuthUtilInterface<T> {
    default HttpServletRequest getRequest(){
        HttpServletRequest request=((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();;
        return request;
    }
}
