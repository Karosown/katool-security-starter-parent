package cn.katool.security.core.model.vo;


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
    private String authRole;
    /**
     *
     */
    private String authPermission;
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


    public AuthVO setoperUser(String operUser) {
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

    public AuthVO setAuthRoles(List authRoleList) {
        this.authRole=JSONUtils.getJSON(authRoleList);
        return this;
    }
    public AuthVO setPermissionCodes(List permissionCodes) {
        this.authPermission=JSONUtils.getJSON(permissionCodes);
        return this;
    }
    public List getAuthRoles(){
        return JSONUtils.getList(this.authRole);
    }
    public List getPermissionCodes(){
        return JSONUtils.getList(this.authPermission);
    }


}