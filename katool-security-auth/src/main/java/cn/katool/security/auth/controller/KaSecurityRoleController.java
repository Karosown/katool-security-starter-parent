package cn.katool.security.auth.controller;

import cn.katool.security.auth.model.KaSecurityRole;
import cn.katool.security.auth.model.dto.role.KaSecurityRoleAddRequest;
import cn.katool.security.auth.model.dto.role.KaSecurityRoleUpdateRequest;
import cn.katool.security.auth.model.graph.AntVEdge;
import cn.katool.security.auth.model.graph.AntVNode;
import cn.katool.security.auth.model.vo.graph.AntVVO;
import cn.katool.security.auth.service.KaSecurityRoleService;
import cn.katool.security.auth.service.impl.KaSecurityRoleServiceImpl;
import cn.katool.security.auth.utils.BaseResponse;
import cn.katool.security.auth.utils.ResultUtils;
import cn.katool.util.cache.utils.CaffeineUtils;
import com.alibaba.excel.util.ListUtils;
import com.alibaba.nacos.shaded.com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/role")
public class KaSecurityRoleController {
    @Resource
    KaSecurityRoleService kaSecurityRoleService;

    @Resource
    CaffeineUtils<Integer,AntVNode> vNodeMap;

    @GetMapping("/tree")
    public BaseResponse<AntVVO> tree(){
        if (vNodeMap.getCache().asMap().isEmpty()){
            kaSecurityRoleService.reload();
        }
        List<Map.Entry<String, Integer>> entries = KaSecurityRoleServiceImpl.ROLES_MAP.entrySet()
                .stream()
                .collect(Collectors.toList());
        List<AntVNode> nodes = entries.stream()
                .map(v -> {
                    AntVNode antVNode = new AntVNode(null,v.getValue(), v.getKey(), null);
                    vNodeMap.put(v.getValue(),antVNode);
                    return antVNode;
                }).collect(Collectors.toList());
        nodes.forEach(item->{
            ArrayList<AntVNode> children = new ArrayList<>();
            Integer pId = item.getName();
            for (int i = 0; i < KaSecurityRoleServiceImpl.UNION_FIND_SETS.size(); i++) {
                Integer parent = KaSecurityRoleServiceImpl.UNION_FIND_SETS.get(i);
                if (null == parent || parent == i || !parent.equals(pId)) {
                    continue;
                }
                children.add(vNodeMap.get(i));
            }
            item.setChildren(children);
        });
        nodes.sort(Comparator.comparing(AntVNode::getName));
        List<AntVEdge> edges = new ArrayList<>();
        for (int i = 0; i < KaSecurityRoleServiceImpl.UNION_FIND_SETS.size(); i++) {
            Integer parent = KaSecurityRoleServiceImpl.UNION_FIND_SETS.get(i);
            if (null == parent || parent == i) {
                continue;
            }
            // 要顺应antv-g6的node编号生成规则
            edges.add(new AntVEdge("node"+(nodes.indexOf(vNodeMap.get(parent))+1),
                    "node"+(nodes.indexOf(vNodeMap.get(i))+1)));
        }
        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setId("node"+(i+1));
        }
        return ResultUtils.success(new AntVVO(nodes,edges));
    }
    // todo 角色表增删改查的接口
    @PostMapping("/")
    public void addRole(KaSecurityRoleAddRequest dto){
        kaSecurityRoleService.save(dto);
    }
    @PutMapping("/")
    public void updateRole(KaSecurityRoleUpdateRequest dto){
        kaSecurityRoleService.update(dto);
    }
    @DeleteMapping("/${id}")
    public void delete(@PathVariable("id")Integer id){
        kaSecurityRoleService.removeById(id);
    }
}
