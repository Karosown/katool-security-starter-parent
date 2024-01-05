package cn.katool.security.demo.gateway.simple.controller;

import cn.katool.security.core.annotation.AuthControllerCheck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/checklogin")
@AuthControllerCheck(onlyCheckLogin = true,
    excludeMethods = {"exclude(String testName)"}
)
public class CheckLoginTestController {

    @GetMapping
    public String lock() {
        return "不出意外这个接口需要检查登录";
    }

    @GetMapping("/unclude")
    public String exclude(String testName) {
        return "这个接口是排除了的";
    }
}
