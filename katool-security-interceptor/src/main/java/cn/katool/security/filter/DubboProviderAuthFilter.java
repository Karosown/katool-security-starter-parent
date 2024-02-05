package cn.katool.security.filter;

import cn.katool.constant.AuthConstant;
import cn.katool.util.auth.AuthUtil;
import com.alibaba.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Activate(group = {Constants.PROVIDER})
public class DubboProviderAuthFilter implements Filter {


    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token = new AuthUtil<Object>().getToken(requestAttributes.getRequest());
        RpcContext.getContext().setAttachment(AuthConstant.TOKEN_HEADER, token);
        return invoker.invoke(invocation);
    }
}