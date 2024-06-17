package cn.katool.security.auth.config;

import cn.katool.util.classes.ClassUtil;
import org.apache.catalina.loader.WebappClassLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


public class Main {
    public static void main(String[] args) {
        ClassUtil classUtil = new ClassUtil();
        classUtil.complieClass(
                System.getProperty ("user.dir")+"/katool-security-plugin-genrous-demo/src/main/java/cn/katool/security/auth/config",
                "AuthLogicPluginDemo"
        );
    }

}