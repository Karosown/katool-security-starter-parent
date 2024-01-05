package cn.katool.security.demo.zuul.simple;

import cn.katool.security.core.annotation.EnableKaSecurityAuthCenter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan(basePackages = "cn.katool.security")
@SpringBootApplication
@EnableKaSecurityAuthCenter
public class ZuulSimpleDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulSimpleDemoApplication.class, args);
    }
}
