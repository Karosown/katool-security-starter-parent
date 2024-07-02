package cn.katool.security.core.config;

import cn.katool.security.core.constant.KaSecurityMode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component("KaSecurityConfig-MODE")
@ConfigurationProperties("katool.security")
public class KaSecurityModeConfig {
    //默认采用单体架构
    private KaSecurityMode mode = KaSecurityMode.SINGLE;

    public static KaSecurityMode currentMode=KaSecurityMode.SINGLE;

    @Bean
    @DependsOn("KaSecurityConfig-MODE")
    private void KaSecurityModeConfigInit(){
        KaSecurityModeConfig.currentMode=mode;
    }
}
