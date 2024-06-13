package cn.katool.security;

import cn.katool.security.core.logic.KaSecurityAuthLogic;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;

import java.util.List;

public class AuthLogicPluginDemo implements KaSecurityAuthLogic {
    @Override
    public KaSecurityValidMessage doAuth(List<String> roleList) {
        return null;
    }

    @Override
    public KaSecurityValidMessage doCheckLogin(Boolean onlyCheckLogin) {
        return KaSecurityAuthLogic.super.doCheckLogin(onlyCheckLogin);
    }
}
