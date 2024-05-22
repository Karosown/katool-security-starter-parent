package cn.katool.security.auth.service;

import cn.katool.security.auth.model.KaSecurityRole;
import cn.katool.security.auth.model.dto.role.KaSecurityRoleAddRequest;
import cn.katool.security.auth.model.dto.role.KaSecurityRoleUpdateRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 30398
* @description 针对表【ka_security_role】的数据库操作Service
* @createDate 2024-02-05 10:36:47
*/
public interface KaSecurityRoleService extends IService<KaSecurityRole> {


    void reload();

    Integer getParentId(Integer id);

    String getParentRole(String role);

    Boolean instanceOf(String role, String parentRole);

    boolean save(KaSecurityRoleAddRequest dto);

    boolean update(KaSecurityRoleUpdateRequest dto);
}
