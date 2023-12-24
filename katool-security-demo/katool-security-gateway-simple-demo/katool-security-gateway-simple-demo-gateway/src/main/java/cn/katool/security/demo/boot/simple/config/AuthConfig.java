package cn.katool.security.demo.boot.simple.config;

import cn.katool.security.core.logic.KaSecurityAuthLogic;
import cn.katool.security.core.logic.KaToolSecurityAuthQueue;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class AuthConfig implements KaSecurityAuthLogic {
    @Override
    public KaSecurityValidMessage checkLogin(Boolean checkLogin) {
        System.out.println("检查登录中");
        return KaSecurityValidMessage.success();
    }

    @Override
    public KaSecurityValidMessage doAuth(List<String> roleList) {
        // 这里可以根据角色列表进行鉴权，返回鉴权失败或者鉴权成功的消息
        System.out.println("进入鉴权，roleList:" + roleList);
        return KaSecurityValidMessage.success();
    }

    @Bean
    private void initAuth(){
        System.out.println("初始化鉴权框架");
        KaToolSecurityAuthQueue.add(this);
    }
}
