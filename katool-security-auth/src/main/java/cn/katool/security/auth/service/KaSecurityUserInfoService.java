package cn.katool.security.auth.service;

import cn.katool.security.auth.model.entity.KaSecurityUser;

public interface KaSecurityUserInfoService {
    KaSecurityUser getLoginUser(String token);

    KaSecurityUser getLoginKaSecurityUserPermitNull(String token);

    boolean isAdmin(String token);

    boolean isAdmin(KaSecurityUser user);


    KaSecurityUser getLoginKaSecurityUser(String token);
}
