package cn.katool.security.auth.model.entity;

import cn.katool.security.core.utils.JSONUtils;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @TableName auth
 */
@TableName(value ="auth")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Auth implements Serializable {
    /**
     * 
     */
    @TableId
    private String id;

    /**
     *
     */
    @TableField(value = "fid")
    private String fid;

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
    @TableField(value = "auth_role")
    private String authRole;

    /**
     * 
     */
    @TableField(value = "oper_user")
    private String operUser;

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

    @TableLogic
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

    public Auth setFid(String fid) {
        this.fid = fid;
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

    public Auth setAuthRole(String authRole) {
        this.authRole = authRole;
        return this;
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

    public Auth setAuthRoles(List authRoleList) {
        this.authRole=JSONUtils.getJSON(authRoleList);
        return this;
    }
    public List getAuthRoles(){
        return JSONUtils.getList(this.authRole);
    }
}