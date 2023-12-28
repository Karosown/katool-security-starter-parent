package cn.katool.security.starter.utils;

import cn.katool.security.core.config.KaSecurityCoreConfig;

import cn.katool.security.core.constant.KaSecurityMode;
import cn.katool.util.auth.AuthUtil;
import com.qiniu.util.Auth;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.yaml.snakeyaml.introspector.PropertySubstitute;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.util.logging.Level;
import java.util.logging.Logger;


public interface DefaultKaSecurityAuthUtilInterface<T> {

    default T getPayLoadWithHeader(){
        return (T) AuthUtil.getPayLoadFromToken(getTokenWithHeader(),
                (Class)((ParameterizedType)
                        getClass().getGenericSuperclass()).getActualTypeArguments()[0]
                );
    }

    default T getPayLoadWithDubboRPC(){
        String token = RpcContext.getContext().getAttachment(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
        return (T) AuthUtil.getPayLoadFromToken(token);
    }
    default T getPayLoad(){
        T payLoad = getPayLoadWithHeader();
        if (ObjectUtils.isEmpty(payLoad)){
            payLoad = getPayLoadWithDubboRPC();
        }
        if (ObjectUtils.isEmpty(payLoad)){
            payLoad = getPayLoadWithDubboRPC();
        }
        return payLoad;
    }

    default String getTokenWithDubboRPC(){
        return RpcContext.getContext().getAttachment(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
    }

    default String getTokenWithHeader(){
        return getTokenWithHeader(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
    }

    public  String getTokenWithHeader(String headerName);
    public  String getTokenWithParameter(String parameterName);
    public  String getTokenWithCookie(String cookieName);
    public  String getTokenWithHeaderOrParameter(String headerName,String parameterName);
}
