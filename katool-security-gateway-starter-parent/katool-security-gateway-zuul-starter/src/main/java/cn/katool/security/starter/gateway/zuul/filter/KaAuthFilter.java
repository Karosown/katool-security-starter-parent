package cn.katool.security.starter.gateway.zuul.filter;


import cn.katool.security.core.logic.KaToolSecurityAuthQueue;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import cn.katool.security.core.utils.JSONUtils;
import cn.katool.security.starter.gateway.core.constant.GlobalContainer;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


// Zuul网关过滤器
@Component
@Slf4j
public class KaAuthFilter extends ZuulFilter {

        @Override
        public String filterType() {
            return FilterConstants.PRE_TYPE;
        }

        @Override
        public int filterOrder() {
            return FilterConstants.PRE_DECORATION_FILTER_ORDER-10;
        }

        @Override
        public boolean shouldFilter() {
            return true;
        }

        @Override
        public Object run() throws ZuulException {
            HttpServletRequest request = RequestContext.getCurrentContext().getRequest();
            String path = request.getRequestURI();
            String requestMethod = request.getMethod();
            for(GlobalContainer.Route v:GlobalContainer.authRouteList){
                String method = v.getMethod();
                String url = v.getUrl();
                if (path.equals(url)&& requestMethod.equals(method)){
                    log.info("url:{} path:{}",url,path);
                    log.info("method:{} name:{}",method,requestMethod);
                    Boolean onlyCheckLogin = v.getOnlyCheckLogin();
                    KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(v.getRole(),v.getPermission(),onlyCheckLogin);
                    if(!KaSecurityValidMessage.success().equals(run)){
                        RequestContext.getCurrentContext().setSendZuulResponse(false);
                        RequestContext.getCurrentContext().setResponseStatusCode(HttpStatus.FORBIDDEN.value());
                        RequestContext.getCurrentContext().setResponseBody(JSONUtils.getJSON(run));
                    }
                    else{
                        // 添加请求头 request
                        RequestContext.getCurrentContext().addZuulRequestHeader("Authed","KaTool-Security");
                    }
                    break;
                }
            }
            return null;
        }
    }

