package cn.katool.security.starter.gateway;

import cn.katool.security.core.annotation.EnableKaSecurityAuthCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

@Component
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Slf4j
@EnableKaSecurityAuthCenter
public class KaSecurityGateWayIniter {

    private final String version = "V1.1.0.SNAPSHOT";

    @Bean
    public void GateWayInit(){
        log.info("KaSecurity-Gateway init success, version:{}", version);
    }
}
