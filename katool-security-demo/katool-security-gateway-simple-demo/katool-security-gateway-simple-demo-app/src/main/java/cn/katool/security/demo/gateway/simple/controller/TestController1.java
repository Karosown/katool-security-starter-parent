package cn.katool.security.demo.gateway.simple.controller;

import cn.katool.security.core.annotation.AuthCheck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TestController1 {
    @GetMapping("/needlogin")
//    @AuthCheck
    @AuthCheck(onlyCheckLogin = true)
    public String needlogin() {
        return "2";
    }
}
