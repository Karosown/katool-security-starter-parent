package cn.katool.security.demo.boot.simple.config;


import cn.katool.security.demo.gateway.simple.config.User;
import cn.katool.security.logic.KaToolSecurityAuthLogicContainer;
import cn.katool.security.starter.utils.KaSecurityAuthUtil;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import cn.katool.security.logic.KaSecurityAuthLogic;
import java.util.List;


@Component
public class AuthConfig extends KaSecurityAuthUtil<User> implements KaSecurityAuthLogic<User>{


    @Override
    public List<String> getUserRoleList() {
        // 正常情况下建议用int或者枚举进行映射
        return this.getPayLoad().getUserRoles();
    }

    @Override
    public List<String> getUserPermissionCodeList() {
        // 正常情况下应该是有专门的权限服务或者读取配置来获取
        return this.getPayLoad().getUserPermissions();
    }


    @Bean
    @Override
    public void loadPlugin() {
        // 加载自定义插件
        KaToolSecurityAuthLogicContainer.insert(0,this);
    }
}
