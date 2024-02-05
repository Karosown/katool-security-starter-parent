package cn.katool.security.auth.service.impl;

import cn.katool.Exception.ErrorCode;
import cn.katool.Exception.KaToolException;
import cn.katool.security.auth.mapper.KaSecurityRoleMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.katool.security.auth.model.KaSecurityRole;
import cn.katool.security.auth.service.KaSecurityRoleService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
* @author 30398
* @description 针对表【ka_security_role】的数据库操作Service实现
* @createDate 2024-02-05 10:36:47
*/
@Service
public class KaSecurityRoleServiceImpl extends ServiceImpl<KaSecurityRoleMapper, KaSecurityRole>
    implements KaSecurityRoleService{

    public static volatile  CopyOnWriteArrayList<String> ROLES = new CopyOnWriteArrayList<>();

    public static volatile ConcurrentHashMap<String,Integer>
            ROLES_MAP = new ConcurrentHashMap<>();

    public static volatile CopyOnWriteArrayList<Integer> UNION_FIND_SETS = new CopyOnWriteArrayList<>();

    @Override
    public void reload(){
        List<KaSecurityRole> roleList = this.list();
        roleList.sort(Comparator.comparing(KaSecurityRole::getId));
        for (KaSecurityRole role : roleList){
            ROLES.add(role.getUserRole());
            if (!ROLES.get(role.getId()).equals(role.getUserRole())){
                ROLES.remove(role.getUserRole());
                ROLES.add(role.getId(),role.getUserRole());
            }
            ROLES_MAP.put(role.getUserRole(),role.getId());
            UNION_FIND_SETS.add(role.getId(),role.getFId());
        }
    }

    @Override
    public Integer getParentId(Integer id){
        return UNION_FIND_SETS.get(id);
    }
    @Override
    public String getParentRole(String role){
        Integer id = ROLES_MAP.get(role);
        return  ROLES.get(getParentId(id));
    }

    @Override
    public Boolean instanceOf(String role, String parentRole){
        if (ROLES_MAP.get(parentRole) == null){
            throw new KaToolException(ErrorCode.PARAMS_ERROR,"父角色不存在");
        }
        Integer roleId = ROLES_MAP.get(role);
        if (roleId == null)
        {
            throw new KaToolException(ErrorCode.PARAMS_ERROR,"角色不存在");
        }
        if (ROLES.get(0).equals(role)&&!ROLES.get(0).equals(parentRole)){
            return false;
        }
        if (getParentRole(role).equals(parentRole)){
            return true;
        }
        return instanceOf(getParentRole(role),parentRole);
    }

}




