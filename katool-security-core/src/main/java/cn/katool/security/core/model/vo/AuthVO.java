package cn.katool.security.core.model.vo;


import cn.katool.security.core.utils.JSONUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @TableName auth
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthVO implements Serializable {
    /**
     *
     */
    private String id;

    /**
     *
     */
    
    private String fid;

    
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
    private String authRole;

    /**
     *
     */
    
    private String operUser;

    /**
     *
     */
    
    private Boolean checkLogin;

    
    private Boolean isOpen;


    /**
     *
     */
    
    private Boolean isDef;

    
    private Integer isDelete;
    
    private static final long serialVersionUID = 1L;


    public AuthVO setId(String id) {
        this.id = id;
        return this;
    }

    public AuthVO setMethod(String method) {
        this.method = method;
        return this;
    }

    public AuthVO setFid(String fid) {
        this.fid = fid;
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


    public AuthVO setoperUser(String operUser) {
        this.operUser = operUser;
        return this;
    }

    public AuthVO setOpen(Boolean open) {
        isOpen = open;
        return this;
    }

    public AuthVO setCheckLogin(Boolean checkLogin) {
        this.checkLogin = checkLogin;
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

    public AuthVO setAuthRoles(List authRoleList) {
        this.authRole=JSONUtils.getJSON(authRoleList);
        return this;
    }
    public List getAuthRoles(){
        return JSONUtils.getList(this.authRole);
    }


}