package cn.katool.security.auth.config;



import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import cn.katool.security.core.model.entity.UserAgentInfo;
import cn.katool.security.logic.KaSecurityAuthLogic;
import cn.katool.security.logic.KaToolSecurityAuthLogicContainer;
import cn.katool.security.starter.utils.KaSecurityAuthUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthLogicPluginDemo extends KaSecurityAuthUtil implements KaSecurityAuthLogic {



    @Override
    public List<String> getUserRoleList() {
        return null;
    }

    @Override
    public List<String> getUserPermissionCodeList() {
        return null;
    }

    @Override
    public void loadPlugin() {

    }


}
