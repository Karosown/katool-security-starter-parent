package cn.katool.security.demo.gateway.simple.controller;

import cn.katool.security.core.annotation.AuthControllerCheck;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@AuthControllerCheck
public class TestController {
    @GetMapping
    public String index() {
        return "1";
    }


}
