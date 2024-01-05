package cn.katool.security.demo.zuul.simple;

import cn.katool.security.core.annotation.EnableKaSecurityAuthCenter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;


@SpringBootApplication
@EnableDiscoveryClient
@EnableKaSecurityAuthCenter
@EnableZuulProxy
public class ZuulSimpleDemoGateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulSimpleDemoGateWayApplication.class, args);
    }
}
