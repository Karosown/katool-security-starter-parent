package cn.katool.security.demo.boot.simple;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@SpringBootApplication
public class SpringBootSimpleDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootSimpleDemoApplication.class, args);
    }
}
