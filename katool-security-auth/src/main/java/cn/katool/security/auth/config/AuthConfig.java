package cn.katool.security.auth.config;

import cn.katool.security.auth.model.entity.KaSecurityUser;
import cn.katool.security.logic.KaSecurityAuthLogic;
import cn.katool.security.logic.KaToolSecurityAuthLogicContainer;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import cn.katool.security.starter.utils.KaSecurityAuthUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
@Slf4j
@Component
public class AuthConfig extends KaSecurityAuthUtil<KaSecurityUser> implements KaSecurityAuthLogic<KaSecurityUser> {
    @Override
    public List<String> getUserRoleList() {
        KaSecurityUser payLoad = this.getPayLoad();
        return Arrays.asList(payLoad.getUserRole());
    }

    @Override
    public List<String> getUserPermissionCodeList() {
        return null;
    }

    @Bean
    @Override
    public void loadPlugin(){
        log.info("AuthConfig init...");
        KaToolSecurityAuthLogicContainer.insert(0,this);
    }


}
