package cn.katool.security.interceptor;


import cn.katool.security.config.KaSecurityModeConfig;
import cn.katool.security.core.annotation.AuthCheck;
import cn.katool.security.core.annotation.AuthServiceCheck;
import cn.katool.security.core.constant.KaSecurityMode;
import cn.katool.security.core.logic.KaToolSecurityAuthQueue;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import cn.katool.security.core.model.vo.AuthVO;
import cn.katool.security.service.AuthService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 权限校验 AOP
 *
*/
@Aspect
@Component
@Slf4j
public class AuthInterceptor {



    @DubboReference(check = false)
    private AuthService authService;



//    private ConcurrentHashMap<String, Boolean> isFirst=new ConcurrentHashMap<>();
    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@within(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取管理信息
        String mustRole = authCheck.mustRole();
        List<String> anyRole = Arrays.stream(authCheck.anyRole())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> excludeList = Arrays.stream(authCheck.excludeMethods()).collect(Collectors.toList());
        String methodName = joinPoint.getSignature().getName();
        log.info("@AuthCheck=>ExcludeList: {}",excludeList);
        log.info("@AuthCheck=>currentMethod: {}",methodName);
        if (excludeList.contains(methodName)){
            return joinPoint.proceed();
        }
        Boolean checkLogin = authCheck.checkLogin();
        // 获取请求信息
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        // 获取路由信息
        if (KaSecurityMode.GATEWAY.equals(KaSecurityModeConfig.currentMode)){
            String method = request.getMethod().toUpperCase(Locale.ROOT);
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            // 判断在gateway上是否鉴权
            String authed = request.getHeader("Authed");
            if (!"gateway".equals(authed)) {
                log.info("\n未被网关鉴权的接口，路由信息如下:\n" +
                        "method: [{}]\n" +
                        "requestURI: [{}]\n" +
                        "contextPath:  [{}]",method,requestURI,contextPath);
                // 通过rpc获取 认证中心 的 route，曾经是否注册过
                AuthVO one = authService.getOne(method, requestURI, contextPath);
                // 如果是新增的路由，那么就加上
                if (ObjectUtils.isEmpty(one)) {
                    log.info("\n新增鉴权路由，路由信息如下:\n" +
                            "method: [{}]\n" +
                            "requestURI: [{}]\n" +
                            "contextPath:  [{}]",method,requestURI,contextPath);
                    one = new AuthVO()
                            .setMethod(method)
                            .setUri(requestURI)
                            .setRoute(contextPath)
                            .setIsDef(true)
                            .setOpen(true);
                }
                // 必须有该权限才通过
                if (StringUtils.isNotBlank(mustRole)) {
                    if (!anyRole.contains(mustRole)) {
                        anyRole.add(mustRole);
                    }
                }
                one.setOpen(true).setIsDef(true).setAuthRoles(anyRole).setCheckLogin(checkLogin).setIsDef(true);    //设置成真值，一样的交给网关执行
                boolean state = authService.saveOrUpdate(one);
                log.info("认证中心保存更新状态state: {}",state);
                // 网关没有鉴权，那么在这里鉴权
                log.info("AOP鉴权开始");
                KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRole,checkLogin);
                if (!run.equals(KaSecurityValidMessage.success())) {
                    return run;
                }
                log.info("AOP鉴权结束");
            }
            else{
                //如果已经鉴权，通过流量染色
                log.info("已在gateway进行鉴权，即将进入业务层，路由信息如下\n" +
                        "method: [{}]\n" +
                        "requestURI: [{}]\n" +
                        "contextPath:  [{}]",method,requestURI,contextPath);
            }
        }
        else {
            log.info("鉴权逻辑执行开始");
            KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRole,checkLogin);
            log.info("鉴权逻辑执行完毕");
            if (!KaSecurityValidMessage.success().equals(run)) {
                return run;
            }
        }
        // 进行响应日志记录
        return handelResponse(joinPoint);
    }

    @Around("@within(authServiceCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthServiceCheck authServiceCheck) throws Throwable {
        // 获取管理信息
        List<String> anyRole = Arrays.stream(authServiceCheck.anyRole())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> excludeList = Arrays.stream(authServiceCheck.excludeMethods()).collect(Collectors.toList());
        String methodName = joinPoint.getSignature().getName();
        log.info("@AuthService=>ExcludeList: {}",excludeList);
        log.info("@AuthService=>currentMethod: {}",methodName);
        if (excludeList.contains(methodName)){
            return joinPoint.proceed();
        }
        // 获取请求信息
        if (RequestContextHolder.getRequestAttributes()==null) {
            // todo: 待解决问题：非HTTP请求如何优化
            return joinPoint.proceed();
        }
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("AOP鉴权开始");
        KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRole,authServiceCheck.checkLogin());
        if (!KaSecurityValidMessage.success().equals(run)) {
            return run;
        }
        log.info("AOP鉴权结束");
        // 进行响应日志记录
        return handelResponse(joinPoint);
    }

    private Object handelResponse(ProceedingJoinPoint joinPoint) throws Throwable {
            return joinPoint.proceed();
    }
}

