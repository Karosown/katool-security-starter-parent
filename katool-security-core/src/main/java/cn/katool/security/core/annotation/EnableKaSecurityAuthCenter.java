package cn.katool.security.core.annotation;


import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.lang.annotation.*;

@EnableDubbo
@EnableScheduling
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface EnableKaSecurityAuthCenter {
}
