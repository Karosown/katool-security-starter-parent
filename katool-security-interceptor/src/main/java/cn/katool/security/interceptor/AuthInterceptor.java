package cn.katool.security.interceptor;


import cn.hutool.core.collection.CollectionUtil;
import cn.katool.constant.AuthConstant;
import cn.katool.security.core.annotation.AuthCheck;
import cn.katool.security.core.annotation.AuthControllerCheck;
import cn.katool.security.core.annotation.AuthServiceCheck;
import cn.katool.security.core.config.KaSecurityModeConfig;
import cn.katool.security.core.constant.KaSecurityAuthCheckMode;
import cn.katool.security.core.constant.KaSecurityMode;
import cn.katool.security.logic.KaToolSecurityAuthQueue;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import cn.katool.security.core.model.vo.AuthVO;
import cn.katool.security.core.utils.JSONUtils;
import cn.katool.security.service.AuthService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
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
        List<String> anyRole = Arrays.stream(authCheck.anyRole())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String>mustRole =  Arrays.stream(authCheck.mustRole()).filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> anyPermissionCodes = Arrays.stream(authCheck.anyPermissionCodes())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> mustPermissionCodes = Arrays.stream(authCheck.mustPermissionCodes()).filter(StringUtils::isNotBlank).collect(Collectors.toList());
        Boolean onlyCheckLogin = authCheck.onlyCheckLogin();
        KaSecurityAuthCheckMode roleMode = authCheck.roleMode();
        KaSecurityAuthCheckMode permissionMode = authCheck.permissionMode();
        List<Integer> logicIndex = Arrays.stream(authCheck.logicIndex()).boxed().collect(Collectors.toList());
        if (RequestContextHolder.getRequestAttributes()==null ) {
            return doInterceptorAuthService("@AuthCheck",joinPoint, anyRole, mustRole, anyPermissionCodes, mustPermissionCodes, onlyCheckLogin,roleMode,permissionMode,logicIndex,null);
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

                one.setOpen(true)
                        .setIsDef(true)
                        .setAnyRole(anyRole)
                        .setMustRole(mustRole)
                        .setAnyPermission(anyPermissionCodes)
                        .setMustPermission(mustPermissionCodes)
                        .setRoleMode(roleMode)
                        .setPermissionMode(permissionMode)
                        .setCheckLogin(onlyCheckLogin)
                        .setIsDef(true);    //设置成真值，一样的交给网关执行
                boolean state = authService.saveOrUpdate(one);
                log.info("[KaTool-Security-AOP-@AuthCheck-Store]认证中心保存更新状态state: {}",state);
                // 网关没有鉴权，那么在这里鉴权
                log.info("[KaTool-Security-AOP-@AuthCheck-Auth]AOP鉴权逻辑执行开始");
                KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRole, mustRole, anyPermissionCodes, mustPermissionCodes, onlyCheckLogin,roleMode,permissionMode,logicIndex);
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
            KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRole, mustRole, anyPermissionCodes, mustPermissionCodes, onlyCheckLogin,roleMode,permissionMode,logicIndex);
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
        List<String> anyRole = Arrays.stream(authControllerCheck.anyRole())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> mustRole = Arrays.stream(authControllerCheck.mustRole())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> anyPermissionCodes = Arrays.stream(authControllerCheck.anyPermissionCodes())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> mustPermissionCodes = Arrays.stream(authControllerCheck.mustPermissionCodes())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> excludeList = Arrays.stream(authControllerCheck.excludeMethods()).collect(Collectors.toList());
        KaSecurityAuthCheckMode roleMode = authControllerCheck.roleMode();
        KaSecurityAuthCheckMode permissionMode = authControllerCheck.permissionMode();
        List<Integer> logicIndex = Arrays.stream(authControllerCheck.logicIndex()).boxed().collect(Collectors.toList());
        String methodName = getFormatCurrentMethodName(joinPoint);
        log.info("[KaTool-Security-AOP-@AuthControllerCheck-Config]@AuthControllerCheck=>CurrentMethod: {}",methodName);
        if (CollectionUtil.isNotEmpty(excludeList)&&excludeList.contains(methodName)){
            log.info("[KaTool-Security-AOP-@AuthControllerCheck-Config]@AuthControllerCheck=>ExcludeList: {}",excludeList);
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
                one.setOpen(true).setIsDef(true)
                        .setAnyRole(anyRole)
                        .setMustRole(mustRole)
                        .setAnyPermission(anyPermissionCodes)
                        .setMustPermission(mustPermissionCodes)
                        .setRoleMode(roleMode)
                        .setPermissionMode(permissionMode)
                        .setLogicIndexs(logicIndex)
                        .setCheckLogin(onlyCheckLogin)
                        .setLogicIndexs(logicIndex)
                        .setIsDef(true);    //设置成真值，一样的交给网关执行
                boolean state = authService.saveOrUpdate(one);
                log.info("[KaTool-Security-AOP-@AuthControllerCheck-Store]认证中心保存更新状态state: {}",state);
                // 网关没有鉴权，那么在这里鉴权
                log.info("[KaTool-Security-AOP-@AuthControllerCheck-Auth]AOP鉴权逻辑执行开始");
                KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRole, mustRole, anyPermissionCodes, mustPermissionCodes, onlyCheckLogin,roleMode,permissionMode,logicIndex);
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
            KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRole, mustRole, anyPermissionCodes, mustPermissionCodes, onlyCheckLogin,roleMode,permissionMode,logicIndex);
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
        // 获取管理信息
        List<String> anyRole = Arrays.stream(authServiceCheck.anyRole())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> mustRole = Arrays.stream(authServiceCheck.mustRole())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> anyPermissionCodes = Arrays.stream(authServiceCheck.anyPermissionCodes())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> mustPermissionCodes = Arrays.stream(authServiceCheck.mustPermissionCodes())
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> excludeList = Arrays.stream(authServiceCheck.excludeMethods()).collect(Collectors.toList());
        KaSecurityAuthCheckMode roleMode = authServiceCheck.roleMode();
        KaSecurityAuthCheckMode permissionMode = authServiceCheck.permissionMode();
        List<Integer> logicIndex = Arrays.stream(authServiceCheck.logicIndex()).boxed().collect(Collectors.toList());
        boolean onlyCheckLogin = authServiceCheck.onlyCheckLogin();
       return doInterceptorAuthService("@AuthServiceCheck",joinPoint,anyRole, mustRole, anyPermissionCodes, mustPermissionCodes, onlyCheckLogin,roleMode,permissionMode,logicIndex,excludeList);
    }

    public Object doInterceptorAuthService(String currentAopMethodName,ProceedingJoinPoint joinPoint, List<String> anyRoleList, List<String> mustRoleList,
                                           List<String> anyPermissionCodeList, List<String> mustPermissionCodeList,
                                           Boolean onlyCheckLogin, KaSecurityAuthCheckMode roleMode, KaSecurityAuthCheckMode permissionMode,List<Integer> logicIndex,List<String> excludeList) throws Throwable {
        String methodName = getFormatCurrentMethodName(joinPoint);
        log.info("[KaTool-Security-AOP-@AuthServiceCheck-Config] {} =>CurrentMethod: {}",currentAopMethodName,methodName);
        if (CollectionUtil.isNotEmpty(excludeList)&&excludeList.contains(methodName)){
            log.info("[KaTool-Security-AOP-@AuthServiceCheck-Config] {} =>ExcludeList: {}",currentAopMethodName,excludeList);
            return joinPoint.proceed();
        }
        // 获取请求信息
        if (RequestContextHolder.getRequestAttributes()==null && StringUtils.isBlank(RpcContext.getContext().getAttachment(AuthConstant.TOKEN_HEADER)) ) {
            return joinPoint.proceed();
        }
        log.info("[KaTool-Security-AOP-@AuthServiceCheck-Auth]AOP鉴权逻辑执行开始");
        KaSecurityValidMessage run = KaToolSecurityAuthQueue.run(anyRoleList, mustRoleList, anyPermissionCodeList, mustPermissionCodeList, onlyCheckLogin, roleMode, permissionMode,logicIndex);
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

