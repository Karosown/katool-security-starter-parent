package cn.katool.security.core.config;

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
@Component("KaSecurityConfig-CORE")
@ConfigurationProperties("katool.security.core")
public class KaSecurityCoreConfig {

    // TOKEN放在哪个请求头中
    String TOKEN_HEADER="Authorization";

    public static String CURRENT_TOKEN_HEADER="Authorization";


    @Bean
    @DependsOn("KaSecurityConfig-CORE")
    private void KaSecurityCoreConfigInit(){
        KaSecurityCoreConfig.CURRENT_TOKEN_HEADER=TOKEN_HEADER;
    }
}
