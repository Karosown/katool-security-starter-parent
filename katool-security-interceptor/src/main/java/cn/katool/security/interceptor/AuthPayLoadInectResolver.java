package cn.katool.security.interceptor;

import cn.katool.Exception.KaToolException;
import cn.katool.security.core.annotation.AuthPayLoad;
import cn.katool.security.core.config.KaSecurityCoreConfig;
import cn.katool.util.auth.AuthUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@Configuration
public class AuthPayLoadInectResolver extends WebMvcConfigurerAdapter implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (parameter.hasParameterAnnotation(AuthPayLoad.class)){
            return true;
        }
        return false;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String tokenKey = KaSecurityCoreConfig.CURRENT_TOKEN_HEADER;
        String token = webRequest.getHeader(tokenKey);
        if (StringUtils.isBlank(token)){
            // 从请求参数中获取token
            token = webRequest.getParameter(tokenKey);
            if (StringUtils.isBlank(token)){
               token = (String) webRequest.getAttribute(tokenKey,0);
            }
        }
        if (StringUtils.isBlank(token)) {
            throw new RuntimeException("未登录");
        }
        Object payLoad = new AuthUtil<Object>().getPayLoadFromToken(token);
        if (null == payLoad){
            throw new RuntimeException("未登录");
        }
        return payLoad;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(this);
    }
}
