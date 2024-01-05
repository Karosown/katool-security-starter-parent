package cn.katool.security.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//项目中的所有接口都支持跨域
                .allowedOriginPatterns("*")//所有地址都可以访问，也可以配置具体地址
                .allowCredentials(true)
                .allowedMethods("*")//
//                Content-Type,Content-Length, Authorization, Accept,X-Requested-With,Uuid,Authorization,X-Ca-Timestamp,X-Ca-Nonce,X-Ca-Sign,ngrok-skip-browser-warning,User-Agent,x-forwarded-for
                .allowedHeaders(
                        "Content-Type",
                        "Content-Length",
                        "Authorization",
                        "Accept",
                        "X-Requested-With",
                        "Uuid",
                        "X-Ca-Timestamp",
                        "X-Ca-Nonce",
                        "X-Ca-Sign",
                        "User-Agent",
                        "x-forwarded-for"
                )
                .exposedHeaders(
                        "*","fileType",
                        "Token","token")
                .maxAge(3600);// 跨域允许时间
    }
}
