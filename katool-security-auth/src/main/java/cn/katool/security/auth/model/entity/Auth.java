package cn.katool.security.auth.model.entity;

import cn.katool.security.core.constant.KaSecurityAuthCheckMode;
import cn.katool.security.core.utils.JSONUtils;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 
 * @TableName auth
 */
@TableName(value ="ka_security_auth")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Auth implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;



    @TableField(value = "method")
    private String method;


    /**
     * 
     */
    @TableField(value = "uri")
    private String uri;

    /**
     * 
     */
    @TableField(value = "route")
    private String route;

    /**
     * 
     */
    @TableField(value = "any_role")
    private String anyRole;

    @TableField(value = "must_role")
    private String mustRole;

    @TableField(value = "any_permission")
    private String anyPermission;
    @TableField(value = "must_permission")
    private String mustPermission;
    @TableField(value = "role_mode")
    private Integer roleMode;
    @TableField(value = "permission_mode")
    private Integer permissionMode;
    @TableField(value = "logic_index")
    private String logicIndex;
    /**
     * 
     */
    @TableField(value = "oper_user")
    private String operUser;

    @TableField(value = "service_name")
    private Boolean serviceName;

    /**
     *
     */
    @TableField(value = "only_check_login")
    private Boolean onlyCheckLogin;


    @TableField(value = "is_open")
    private Boolean isOpen;
    /**
     * 
     */
    @TableField(value = "is_def")
    private Boolean isDef;

    /**
     *
     */
    @TableField(value = "created_time")
    private Date createdTime;

    /**
     *
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableLogic(value = "0",delval = "1")
    @TableField(value = "is_delete")
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public Auth setId(String id) {
        this.id = id;
        return this;
    }

    public Auth setMethod(String method) {
        this.method = method;
        return this;
    }

    public Auth setUri(String uri) {
        this.uri = uri;
        return this;
    }

    public Auth setRoute(String route) {
        this.route = route;
        return this;
    }

    public Auth setAnyRole(String authRole) {
        this.anyRole = authRole;
        return this;
    }
    public Auth setMustRole(String authRole) {
        this.mustRole = authRole;
        return this;
    }
    public Auth setAnyPermission(String authRole) {
        this.anyPermission = authRole;
        return this;
    }
    public Auth setMustPermission(String authRole) {
        this.mustPermission = authRole;
        return this;
    }
    public Auth setRoleMode(Integer roleMode) {
        this.roleMode = roleMode;
        return this;
    }
    public Auth setPermissionMode(Integer permissionMode) {
        this.permissionMode = permissionMode;
        return this;
    }
    public Auth setRoleMode(KaSecurityAuthCheckMode mode){
        this.roleMode = mode.getMode();
        return this;
    }
    public Auth setPermissionMode(KaSecurityAuthCheckMode mode){
        this.permissionMode = mode.getMode();
        return this;
    }
    public KaSecurityAuthCheckMode getRoleModeEnum(){
        switch (this.roleMode){
            case 0: return KaSecurityAuthCheckMode.OR;
            case 1: return KaSecurityAuthCheckMode.AND;
            default:
                throw new RuntimeException("读取RoleMode异常，请检测authId="+this.id);
        }
    }

    public Auth setOperUser(String operUser) {
        this.operUser = operUser;
        return this;
    }

    public Auth setOpen(Boolean open) {
        isOpen = open;
        return this;
    }

    public Auth setCheckLogin(Boolean onlyCheckLogin) {
        this.onlyCheckLogin = onlyCheckLogin;
        return this;
    }

    public Auth setIsDef(Boolean isDef) {
        this.isDef = isDef;
        return this;
    }

    public Auth setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
        return this;
    }

    public Auth setAnyRoles(List authRoleList) {
        this.anyRole=JSONUtils.getJSON(authRoleList);
        return this;
    }
    public List getAnyRoles(){
        return JSONUtils.getList(this.anyRole);
    }
    public Auth setMustRoles(List authRoleList) {
        this.mustRole=JSONUtils.getJSON(authRoleList);
        return this;
    }
    public List getMustRoles(){
        return JSONUtils.getList(this.mustRole);
    }
    public Auth setAnyPermissions(List authPermissionList) {
        this.anyPermission=JSONUtils.getJSON(authPermissionList);
        return this;
    }
    public List getAnyPermissions(){
        return JSONUtils.getList(this.anyPermission);
    }
    public Auth setMustPermissions(List authPermissionList) {
        this.mustPermission=JSONUtils.getJSON(authPermissionList);
        return this;
    }
    public List getMustPermissions(){
        return JSONUtils.getList(this.mustPermission);
    }

    public Auth setLogicIndexs(List logicIndex) {
        this.logicIndex=JSONUtils.getJSON(logicIndex);
        return this;
    }
    public List getLogicIndexs(){
        return JSONUtils.getList(this.logicIndex);
    }
}