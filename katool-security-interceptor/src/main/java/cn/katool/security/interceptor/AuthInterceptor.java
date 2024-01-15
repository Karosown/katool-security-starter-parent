package cn.katool.security.interceptor;


import cn.hutool.core.collection.CollectionUtil;
import cn.katool.constant.AuthConstant;
import cn.katool.security.config.KaSecurityModeConfig;
import cn.katool.security.core.annotation.AuthCheck;
import cn.katool.security.core.annotation.AuthControllerCheck;
import cn.katool.security.core.annotation.AuthServiceCheck;
import cn.katool.security.core.constant.KaSecurityMode;
import cn.katool.security.core.logic.KaToolSecurityAuthQueue;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import cn.katool.security.core.model.vo.AuthVO;
import cn.katool.security.core.utils.JSONUtils;
import cn.katool.security.service.AuthService;

import cn.katool.util.auth.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.DubboSpringInitContext;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.cluster.router.mesh.rule.virtualservice.DubboMatchRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    
    @Resource
    Environment environment;
    /**
     * 执行拦截
     *
     * @param joinPoint
     * @param authCheck
     * @return
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptorAuthCheck(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 获取管理信息
        String mustRole = authCheck.mustRole();
        List<String> anyRole = Arrays.stream(authCheck.anyRole())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        Boolean onlyCheckLogin = authCheck.onlyCheckLogin();
        if (StringUtils.isNotBlank(mustRole)) {
            if (!anyRole.contains(mustRole)) {
                anyRole.add(mustRole);
            }
        }
        if (RequestContextHolder.getRequestAttributes()==null ) {

            return doInterceptorAuthService(joinPoint,anyRole,null,onlyCheckLogin);
        }
        // 获取请求信息
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        // 获取路由信息
        if (!KaSecurityMode.SINGLE.equals(KaSecurityModeConfig.currentMode)){
            String method = request.getMethod().toUpperCase(Locale.ROOT);
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            // 判断在gateway上是否鉴权
            String authed = request.getHeader("Authed");
            if (!"KaTool-Security".equals(authed)) {
                log.info("\n[KaTool-Security-AOP-@AuthCheck-UnGateWay]路由信息如下:\n" +
                        "method: [{}]\n" +
                        "requestURI: [{}]\n" +
                        "contextPath:  [{}]",method,requestURI,contextPath);
                // 通过rpc获取 认证中心 的 route，曾经是否注册过
                AuthVO one = authService.getOne(method, requestURI, contextPath);
                // 如果是新增的路由，那么就加上
                if (ObjectUtils.isEmpty(one)) {
                    log.info("\n[KaTool-Security-AOP-@AuthCheck-Store]新增鉴权路由，路由信息如下:\n" +
                            "method: [{}]\n" +
                            "requestURI: [{}]\n" +
                            "contextPath:  [{}]",method,requestURI,contextPath);
                    one = new AuthVO()
                            .setMethod(method)
                            .setUri(requestURI)
                            .setRoute(contextPath)
                            .setIsDef(true)
                            .setOpen(true)
                            .setServiceName(environment.getProperty("spring.application.name"));
                }
                // 必须有该权限才通过
                if (StringUtils.isNotBlank(mustRole)) {
                    if (!anyRole.contains(mustRole)) {
                        anyRole.add(mustRole);
                    }
                }
                one.setOpen(true).setIsDef(true).setAuthRoles(anyRole).setCheckLogin(onlyCheckLogin).setIsDef(true);    //设置成真值，一样的交给网关执行
                boolean state = authService.saveOrUpdate(one);
                log.info("[KaTool-Security-AOP-@AuthCheck-Store]认证中心保存更新状态state: {}",state);
                // 网关没有鉴权，那么在这里鉴权
                log.info("[KaTool-Security-AOP-@AuthCheck-Auth]AOP鉴权逻辑执行开始");
                KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRole,onlyCheckLogin);
                if (!KaSecurityValidMessage.success().equals(run)) {
                    return responseHandler(JSONUtils.getJSON(run));
                }
                log.info("[KaTool-Security-AOP-@AuthCheck-Auth]AOP鉴权逻辑执行完毕");
            }
            else{
                //如果已经鉴权，通过流量染色
                log.info("[KaTool-Security-AOP-@AuthCheck-Authed]即将进入业务层，路由信息如下\n" +
                        "method: [{}]\n" +
                        "requestURI: [{}]\n" +
                        "contextPath:  [{}]",method,requestURI,contextPath);
            }
        }
        else {
            log.info("[KaTool-Security-AOP-@AuthCheck-Auth]鉴权逻辑执行开始");
            KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRole,onlyCheckLogin);
            log.info("[KaTool-Security-AOP-@AuthCheck-Auth]鉴权逻辑执行完毕");
            if (!KaSecurityValidMessage.success().equals(run)) {
                return responseHandler(JSONUtils.getJSON(run));
            }
        }
        // 进行响应日志记录
        return handelResponse(joinPoint);
    }



    @Around("@within(authControllerCheck)")
    public Object doInterceptorAuthCheckController(ProceedingJoinPoint joinPoint, AuthControllerCheck authControllerCheck) throws Throwable {
        // 获取管理信息
        String mustRole = authControllerCheck.mustRole();
        List<String> anyRole = Arrays.stream(authControllerCheck.anyRole())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        // 必须有该权限才通过
        if (StringUtils.isNotBlank(mustRole)) {
            if (!anyRole.contains(mustRole)) {
                anyRole.add(mustRole);
            }
        }
        List<String> excludeList = Arrays.stream(authControllerCheck.excludeMethods()).collect(Collectors.toList());
        log.info("[KaTool-Security-AOP-@AuthControllerCheck-Config]@AuthControllerCheck=>ExcludeList: {}",excludeList);
        String methodName = getFormatCurrentMethodName(joinPoint);
        log.info("[KaTool-Security-AOP-@AuthControllerCheck-Config]@AuthControllerCheck=>CurrentMethod: {}",methodName);
        if (CollectionUtil.isNotEmpty(excludeList)&&excludeList.contains(methodName)){
            return joinPoint.proceed();
        }
        Boolean onlyCheckLogin = authControllerCheck.onlyCheckLogin();
        // 获取请求信息
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        // 获取路由信息
        if (!KaSecurityMode.SINGLE.equals(KaSecurityModeConfig.currentMode)){
            String method = request.getMethod().toUpperCase(Locale.ROOT);
            String requestURI = request.getRequestURI();
            String contextPath = request.getContextPath();
            // 判断在gateway上是否鉴权
            String authed = request.getHeader("Authed");
            if (!"KaTool-Security".equals(authed)) {
                log.info("\n[KaTool-Security-AOP-@AuthControllerCheck-UnGateWay]路由信息如下:\n" +
                        "method: [{}]\n" +
                        "requestURI: [{}]\n" +
                        "contextPath:  [{}]",method,requestURI,contextPath);
                // 通过rpc获取 认证中心 的 route，曾经是否注册过
                AuthVO one = authService.getOne(method, requestURI, contextPath);
                // 如果是新增的路由，那么就加上
                if (ObjectUtils.isEmpty(one)) {
                    log.info("\n[KaTool-Security-AOP-@AuthControllerCheck-Store]路由信息如下:\n" +
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
                one.setOpen(true).setIsDef(true).setAuthRoles(anyRole).setCheckLogin(onlyCheckLogin).setIsDef(true);    //设置成真值，一样的交给网关执行
                boolean state = authService.saveOrUpdate(one);
                log.info("[KaTool-Security-AOP-@AuthControllerCheck-Store]认证中心保存更新状态state: {}",state);
                // 网关没有鉴权，那么在这里鉴权
                log.info("[KaTool-Security-AOP-@AuthControllerCheck-Auth]AOP鉴权逻辑执行开始");
                KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRole,onlyCheckLogin);
                if (!KaSecurityValidMessage.success().equals(run)) {
                    return responseHandler(JSONUtils.getJSON(run));
                }
                log.info("[KaTool-Security-AOP-@AuthControllerCheck-Auth]AOP鉴权逻辑执行结束");
            }
            else{
                //如果已经鉴权，通过流量染色
                log.info("[KaTool-Security-AOP-@AuthControllerCheck-Authed]AOP鉴权逻辑执行即将进入业务层，路由信息如下\n" +
                        "method: [{}]\n" +
                        "requestURI: [{}]\n" +
                        "contextPath:  [{}]",method,requestURI,contextPath);
            }
        }
        else {
            log.info("[KaTool-Security-AOP-@AuthControllerCheck-Auth]AOP鉴权逻辑执行开始");
            KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRole,onlyCheckLogin);
            log.info("[KaTool-Security-AOP-@AuthControllerCheck-Auth]AOP鉴权逻辑执行结束");
            if (!KaSecurityValidMessage.success().equals(run)) {
                return responseHandler(JSONUtils.getJSON(run));
            }
        }
        // 进行响应日志记录
        return handelResponse(joinPoint);
    }
    @Around("@within(authServiceCheck)")
    public Object doInterceptorAuthService(ProceedingJoinPoint joinPoint, AuthServiceCheck authServiceCheck) throws Throwable {
        // 获取管理信息
        List<String> anyRole = Arrays.stream(authServiceCheck.anyRole())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        String mustRole = authServiceCheck.mustRole();
        if (StringUtils.isNotBlank(mustRole)) {
            if (!anyRole.contains(mustRole)) {
                anyRole.add(mustRole);
            }
        }
        List<String> excludeList = Arrays.stream(authServiceCheck.excludeMethods()).collect(Collectors.toList());
        return doInterceptorAuthService(joinPoint,anyRole,excludeList,authServiceCheck.onlyCheckLogin());
    }

    public Object doInterceptorAuthService(ProceedingJoinPoint joinPoint,List<String> anyRole,List<String> excludeList,boolean onlyCheckLogin) throws Throwable {
        log.info("[KaTool-Security-AOP-@AuthServiceCheck-Config]@AuthServiceCheck=>ExcludeList: {}",excludeList);
        String methodName = getFormatCurrentMethodName(joinPoint);
        log.info("[KaTool-Security-AOP-@AuthServiceCheck-Config]@AuthServiceCheck=>CurrentMethod: {}",excludeList);
        if (CollectionUtil.isNotEmpty(excludeList)&&excludeList.contains(methodName)){
            return joinPoint.proceed();
        }
        // 获取请求信息
        if (RequestContextHolder.getRequestAttributes()==null && StringUtils.isBlank(RpcContext.getContext().getAttachment(AuthConstant.TOKEN_HEADER)) ) {
            return joinPoint.proceed();
        }
        log.info("[KaTool-Security-AOP-@AuthServiceCheck-Auth]AOP鉴权逻辑执行开始");
        KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRole,onlyCheckLogin);
        if (!KaSecurityValidMessage.success().equals(run)) {
            return responseHandler(JSONUtils.getJSON(run));
        }
        log.info("[KaTool-Security-AOP-@AuthServiceCheck-Auth]AOP鉴权逻辑执行结束");
        // 进行响应日志记录
        return handelResponse(joinPoint);
    }

    private Object responseHandler(String json) {
        // 获取Response
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        // 将json写入Response
        try {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            // 将响应体修改为json;
            response.getOutputStream()
                    .write(json.getBytes(StandardCharsets.UTF_8));
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    private String getFormatCurrentMethodName(JoinPoint joinPoint) {
        StringBuffer methodName = new StringBuffer(joinPoint.getSignature().getName()+"(");
        // 获取参数类型
        MethodSignature currentMethodSignature = (MethodSignature) joinPoint.getSignature();
        Method cuurentMethod = currentMethodSignature.getMethod();
        Class<?>[] parameterTypes = cuurentMethod.getParameterTypes();
        String[] parameterNames = currentMethodSignature.getParameterNames();
        Boolean hasPram = false;
        for (int i = 0, parameterTypesLength = parameterTypes.length; i < parameterTypesLength; i++) {
            hasPram=true;
            Class<?> parameterType = parameterTypes[i];
            methodName.append(parameterType.getSimpleName())
                    .append(' ')
                    .append(parameterNames[i])
                    .append(",");
        }
        if (hasPram) {
            methodName.delete(methodName.length() - 1, methodName.length());
        }
        methodName.append(")");
        return methodName.toString();
    }
    private Object handelResponse(ProceedingJoinPoint joinPoint) throws Throwable {
            return joinPoint.proceed();
    }
}

