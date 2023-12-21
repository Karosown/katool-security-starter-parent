package cn.katool.security.auth;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableKnife4j
@SpringBootApplication(scanBasePackages = {"cn.katool.security"})
@MapperScan("cn.katool.security.*")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableDubbo
@EnableTransactionManagement
@EnableScheduling
public class ThinkTankAuth {
    public static void main(String[] args) {
        SpringApplication.run(ThinkTankAuth.class,args);
    }
}