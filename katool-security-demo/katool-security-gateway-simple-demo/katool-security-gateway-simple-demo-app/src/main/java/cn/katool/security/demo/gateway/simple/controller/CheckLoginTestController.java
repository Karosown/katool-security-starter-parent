package cn.katool.security.demo.boot.simple.controller;

import cn.katool.security.core.annotation.AuthCheck;
import cn.katool.security.core.annotation.AuthControllerCheck;
import cn.katool.security.core.constant.KaSecurityAuthCheckMode;
import cn.katool.security.demo.gateway.simple.config.User;
import cn.katool.security.starter.utils.KaSecurityAuthUtil;
import cn.katool.util.auth.AuthUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/checklogin")
@AuthControllerCheck(onlyCheckLogin = true,
        excludeMethods = {"exclude(String testName)","touchToken(User user)"}
)
public class CheckLoginTestController {

    @GetMapping
    @AuthCheck
    public String lock() {
        return "不出意外这个接口需要检查登录";
    }

    @GetMapping("/unclude")
    public String exclude(String testName) {
        return "这个接口是排除了的";
    }

    @GetMapping("/valid/role/any")
    @AuthCheck(anyRole = {"user","admin"})
    public String validUserRole() {
        return "访问成功";
    }

    @GetMapping("/valid/role/must")
    @AuthCheck(mustRole = {"admin"})
    public String validUserRole2() {
        return "访问成功";
    }

    @GetMapping("/valid/role/or")
    @AuthCheck(anyRole = {"user","admin"}, mustRole = {"test"},roleMode = KaSecurityAuthCheckMode.OR)
    public String validUserRole3() {
        return "访问成功";
    }

    @GetMapping("/valid/role/and")
    @AuthCheck(anyRole = {"user","admin"}, mustRole = {"test"},roleMode = KaSecurityAuthCheckMode.AND)
    public String validUserRole4() {
        return "访问成功";
    }

    @GetMapping("/valid/permission/any")
    @AuthCheck(anyPermissionCodes = {"user:read","admin:write"})
    public String validUserPermission() {
        return "访问成功";
    }
    @GetMapping("/valid/permission/must")
    @AuthCheck(mustPermissionCodes = {"admin:write"})
    public String validUserPermission2() {
        return "访问成功";
    }
    @GetMapping("/valid/permission/or")
    @AuthCheck(anyPermissionCodes = {"user:read","admin:write"}, mustPermissionCodes = {"test:delete"},permissionMode = KaSecurityAuthCheckMode.OR)
    public String validUserPermission3() {
        return "访问成功";
    }
    @GetMapping("/valid/permission/and")
    @AuthCheck(anyPermissionCodes = {"user:read","admin:write"}, mustPermissionCodes = {"test:delete"},permissionMode = KaSecurityAuthCheckMode.AND)
    public String validUserPermission4() {
        return "访问成功";
    }
    @GetMapping("/valid/mix/any")
    @AuthCheck(anyRole = {"user","admin"}, anyPermissionCodes = {"user:read","admin:write"})
    public String validUserMix() {
        return "访问成功";
    }
    @GetMapping("/valid/mix/must")
    @AuthCheck(mustRole = {"admin"}, mustPermissionCodes = {"admin:write"})
    public String validUserMix2() {
        return "访问成功";
    }
    @GetMapping("/valid/mix/or")
    @AuthCheck(anyRole = {"user","admin"}, anyPermissionCodes = {"user:read","admin:write"}, mustRole = {"test"}, mustPermissionCodes = {"test:delete"},        roleMode = KaSecurityAuthCheckMode.OR, permissionMode = KaSecurityAuthCheckMode.OR)
    public String validUserMix3() {
        return "访问成功";
    }
    @GetMapping("/valid/mix/and")
    @AuthCheck(anyRole = {"user","admin"}, anyPermissionCodes = {"user:read","admin:write"}, mustRole = {"test"}, mustPermissionCodes = {"test:delete"},
            roleMode = KaSecurityAuthCheckMode.AND, permissionMode = KaSecurityAuthCheckMode.AND
    )
    public String validUserMix4() {
        return "访问成功";
    }
    @Resource
    KaSecurityAuthUtil<User> util;
    @GetMapping("/touch/token")
    public String touchToken(User user) {
        String token = util.login(user);
        return token;
    }
}
