package cn.katool.security.auth.config;

import cn.katool.security.auth.model.entity.KaSecurityUser;
import cn.katool.security.core.logic.KaSecurityAuthLogic;
import cn.katool.security.core.logic.KaToolSecurityAuthQueue;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import cn.katool.security.starter.utils.KaSecurityAuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
public class AuthConfig extends KaSecurityAuthUtil<KaSecurityUser> implements KaSecurityAuthLogic {
    @Override
    public KaSecurityValidMessage doCheckLogin(Boolean onlyCheckLogin) {
        Boolean login = this.isLogin();
        if (!login){
            return KaSecurityValidMessage.unLogin();
        }
        if (BooleanUtils.isFalse(onlyCheckLogin)){
            log.info("当前接口不仅仅检查登录情况");
            return KaSecurityValidMessage.success();
        }
        return KaSecurityValidMessage.onlyLogin();
    }

    @Override
    public KaSecurityValidMessage doAuth(List<String> roleList,List<String> permissionCodesList) {
        KaSecurityUser payLoad = this.getPayLoad();
        String userRole = payLoad.getUserRole();
        if (roleList.contains(userRole)){
            return KaSecurityValidMessage.success();
        }
        return KaSecurityValidMessage.noAuth();
    }
    @Bean
    @Override
    public void loadPlugin(){
        log.info("AuthConfig init...");
        KaToolSecurityAuthQueue.insert(0,this);
    }
}
