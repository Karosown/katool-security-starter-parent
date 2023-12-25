package cn.katool.security.starter.gatway;

import cn.katool.security.core.annotation.EnableKaSecurityAuthCenter;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Slf4j
@EnableKaSecurityAuthCenter
public class KaSecurityGateWayIniter {

    private final String version = "V1.0.0.SNAPSHOT";

    @Bean
    public void GateWayInit(){
        log.info("KaSecurity-Gateway init success, version:{}", version);
    }
}
