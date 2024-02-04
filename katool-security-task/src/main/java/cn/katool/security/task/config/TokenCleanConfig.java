package cn.katool.security.task.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ConfigurationPropertiesScan("katool.security.task.clean-token")
public class TokenCleanConfig {

    // 定期删除计划多久执行一次
    String corn;

    // 每次最多删除多少token，如果为60，那么至少会留下40%的token
    Integer effort;

    public static String corn_config;
    public static Integer effort_config;

    @Bean
    void init(){
        corn_config = corn;
        effort_config = effort;
    }

}
