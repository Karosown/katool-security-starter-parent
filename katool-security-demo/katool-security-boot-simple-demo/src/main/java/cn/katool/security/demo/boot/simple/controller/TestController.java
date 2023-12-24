package cn.katool.security.demo.boot.simple.controller;

import cn.katool.security.core.annotation.AuthCheck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@AuthCheck(checkLogin = true)
public class TestController {
    @GetMapping
    public String index() {
        return "1";
    }
}
