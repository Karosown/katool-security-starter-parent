package cn.katool.security.auth;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@EnableKnife4j
@SpringBootApplication(scanBasePackages = {"cn.katool.security.auth", "cn.katool.security.core","cn.katool.security.starter.utils"})
@MapperScan("cn.katool.security.auth.mapper")
@EnableDubbo
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableScheduling
public class KaToolSecurityAuthCenterApplication {
    public static void main(String[] args) {
        SpringApplication.run(KaToolSecurityAuthCenterApplication.class,args);
    }
}