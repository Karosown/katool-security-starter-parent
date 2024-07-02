package cn.katool.security.core.model.vo;


import cn.katool.security.core.constant.KaSecurityAuthCheckMode;
import cn.katool.security.core.utils.JSONUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 * @TableName auth
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode
public class AuthVO implements Serializable {
    /**
     *
     */
    private String id;

    /**
     *
     */
    


    
    private String method;


    /**
     *
     */
    
    private String uri;

    /**
     *
     */
    
    private String route;

    /**
     *
     */
    private String anyRole;
    /**
     *
     */
    private String anyPermission;
    private String mustRole;
    private String mustPermission;
    private Integer roleMode;
    private Integer permissionMode;
    private String logicIndex;
    /**
     *
     */
    
    private String operUser;

    /**
     *
     */

    private String serviceName;

    /**
     *
     */
    
    private Boolean onlyCheckLogin;

    
    private Boolean isOpen;


    /**
     *
     */
    
    private Boolean isDef;

    
    private Integer isDelete;
    /**
     *
     */
    private Date createdTime;

    /**
     *
     */
    private Date updateTime;
    
    private static final long serialVersionUID = 1L;


    public AuthVO setId(String id) {
        this.id = id;
        return this;
    }

    public AuthVO setMethod(String method) {
        this.method = method;
        return this;
    }



    public AuthVO setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public AuthVO setRoute(String route) {
        this.route = route;
        return this;
    }


    public AuthVO setOperUser(String operUser) {
        this.operUser = operUser;
        return this;
    }

    public AuthVO setOpen(Boolean open) {
        isOpen = open;
        return this;
    }

    public AuthVO setCheckLogin(Boolean onlyCheckLogin) {
        this.onlyCheckLogin = onlyCheckLogin;
        return this;
    }

    public AuthVO setIsDef(Boolean isDef) {
        this.isDef = isDef;
        return this;
    }

    public AuthVO setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
        return this;
    }

    public AuthVO setAnyRole(List authRoleList) {
        this.anyRole=JSONUtils.getJSON(authRoleList);
        return this;
    }
    public AuthVO setAnyPermission(List permissionCodes) {
        this.anyPermission = JSONUtils.getJSON(permissionCodes);
        return this;
    }
    public List getAnyRole(){
        return JSONUtils.getList(this.anyRole);
    }
    public List getAnyPermission(){
        return JSONUtils.getList(this.anyPermission);
    }
    public AuthVO setMustRole(List authRoleList) {
        this.mustRole=JSONUtils.getJSON(authRoleList);
        return this;
    }
    public AuthVO setMustPermission(List permissionCodes) {
        this.mustPermission = JSONUtils.getJSON(permissionCodes);
        return this;
    }
    public List getMustRole(){
        return JSONUtils.getList(this.mustRole);
    }
    public List getMustPermission(){
        return JSONUtils.getList(this.mustPermission);
    }

    public KaSecurityAuthCheckMode getRoleMode() {
        return roleMode==1?KaSecurityAuthCheckMode.AND:KaSecurityAuthCheckMode.OR;
    }

    public AuthVO setRoleMode(KaSecurityAuthCheckMode roleMode) {
        this.roleMode = roleMode.getMode();
        return this;
    }
    public AuthVO setRoleMode(Integer roleMode) {
        this.roleMode = roleMode;
        return this;
    }

    public KaSecurityAuthCheckMode getPermissionMode() {
        return permissionMode==1?KaSecurityAuthCheckMode.AND:KaSecurityAuthCheckMode.OR;
    }

    public AuthVO setPermissionMode(KaSecurityAuthCheckMode permissionMode) {
        this.permissionMode = permissionMode.getMode();
        return this;
    }
    public AuthVO setPermissionMode(Integer permissionMode) {
        this.permissionMode = permissionMode;
        return this;
    }
    public List<String> getLogicIndex() {
        return JSONUtils.getList(this.logicIndex);
    }

    public void setLogicIndex(String logicIndex) {
        this.logicIndex = logicIndex;
    }

    public AuthVO setLogicIndexs(List<Integer> logicIndex) {
        this.logicIndex = JSONUtils.getJSON(logicIndex);
        return this;
    }
}