package cn.katool.security.demo.gateway.simple.config;

import cn.hutool.core.util.BooleanUtil;
import cn.katool.security.core.logic.KaSecurityAuthLogic;
import cn.katool.security.core.logic.KaToolSecurityAuthQueue;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import cn.katool.security.starter.utils.KaSecurityAuthUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthConfig extends KaSecurityAuthUtil<String> implements KaSecurityAuthLogic{
    @Override
    public KaSecurityValidMessage checkLogin(Boolean checkLogin) {
        if (BooleanUtil.isFalse(checkLogin)){
            return KaSecurityValidMessage.success();
        }
            String payLoad = this.getPayLoad();
        return KaSecurityValidMessage.unLogin();
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
