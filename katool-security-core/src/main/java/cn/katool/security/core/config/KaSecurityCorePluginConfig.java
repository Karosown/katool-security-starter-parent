package cn.katool.security.core.config;

import cn.katool.security.core.logic.KaSecurityAuthLogic;
import cn.katool.util.classes.ClassUtil;
import cn.katool.util.classes.SpringContextUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@ConfigurationProperties("katool.security.plugin")
public class KaSecurityCorePluginConfig {
    /**
     * 是否开启插件化插入（插件化控制 > 配置类控制）
     */
    public Boolean enable = false;
    public List<String> classUrls = null;

    public String packageName = null;

    @Resource
    ClassUtil classUtil;

    @Bean("KaSecurityConfig-PLUGIN")
    @RefreshScope
    public void load(){
        if (!BooleanUtils.isTrue(enable) || ObjectUtils.isEmpty(classUrls)){
            return ;
        }
        classUrls.forEach(classUrl->{
            String className=classUrl.substring(classUrl.lastIndexOf('/') + 1, classUrl.lastIndexOf(".class"));
            Class aClass = classUtil.urlLoader(classUrl, packageName+"."+className);
            KaSecurityAuthLogic logic;
            try {
                logic = (KaSecurityAuthLogic) aClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            SpringContextUtils.regBean(className,logic);
        });
    }
}
