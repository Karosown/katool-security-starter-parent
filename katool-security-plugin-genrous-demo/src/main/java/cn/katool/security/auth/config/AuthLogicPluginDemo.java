package cn.katool.security.auth.config;


import cn.katool.security.core.logic.KaSecurityAuthLogic;
import cn.katool.security.core.logic.KaToolSecurityAuthQueue;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthLogicPluginDemo implements KaSecurityAuthLogic {


    @Override
    public KaSecurityValidMessage doAuth(List<String> roleList, List<String> permissionCodeList) {
        System.out.println("插件测试");
        return KaSecurityValidMessage.success();
    }

    @Override
    public KaSecurityValidMessage doCheckLogin(Boolean onlyCheckLogin) {
        System.out.println("插件测试");
        return KaSecurityValidMessage.success();
    }
    @Bean
    @Override
    public void loadPlugin(){
        KaToolSecurityAuthQueue.insert(0,this);
    }
}
