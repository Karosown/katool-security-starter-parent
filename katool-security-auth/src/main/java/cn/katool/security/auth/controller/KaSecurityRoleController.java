package cn.katool.security.auth.controller;

import cn.katool.security.auth.model.KaSecurityRole;
import cn.katool.security.auth.model.dto.role.KaSecurityRoleAddRequest;
import cn.katool.security.auth.model.dto.role.KaSecurityRoleUpdateRequest;
import cn.katool.security.auth.service.KaSecurityRoleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api//role")
public class KaSecurityRoleController {
    @Resource
    KaSecurityRoleService kaSecurityRoleService;
    // todo 角色表增删改查的接口
    @PostMapping("/")
    public void addRole(KaSecurityRoleAddRequest dto){
        kaSecurityRoleService.save(dto);
    }
    // todo 查询返回树状图，具体的数据结构参考ELementUI
    @PutMapping("/")
    public void updateRole(KaSecurityRoleUpdateRequest dto){
        kaSecurityRoleService.update(dto);
    }
}
