/**
 * Title
 * 进行接口鉴权核对，如果用户权限不是指定权限，那么就失效
 * @ClassName: ValidRole
 * @Description:
 * @author: 巫宗霖
 * @date: 2023/5/27 12:11
 * @Blog: https://www.wzl1.top/
 */

package cn.katool.security.auth.aop;



import cn.katool.security.auth.model.KaSecurityUser;
import cn.katool.security.auth.exception.BusinessException;
import cn.katool.security.auth.exception.ErrorCode;
import cn.katool.security.core.config.KaSecurityCoreConfig;
import cn.katool.util.auth.AuthUtil;
import cn.katool.util.verify.IPUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class ValidRole {

    @Around("execution(* cn.katool.security.auth.controller.*.*(..)) && !execution(* cn.katool.security.auth.controller.AuthController.uuid(..))")
    public Object Valid(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.getRequestAttributes())).getRequest();
        String authorization = request.getHeader(KaSecurityCoreConfig.CURRENT_TOKEN_HEADER);
        KaSecurityUser KaSecurityUser = (KaSecurityUser) AuthUtil.getUserFromToken(authorization,KaSecurityUser.class);
        if (ObjectUtils.isEmpty(KaSecurityUser)){
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        String ipAddr = IPUtils.getIpAddr(request);
        log.info("Begin => IP:{} {} 进行操作{}",ipAddr,request.getMethod(),request.getRequestURL());
        Object proceed = point.proceed();
        log.info("End   => IP:{} {} 进行操作{}",ipAddr,request.getMethod(),request.getRequestURL());
        return proceed;
    }
}
