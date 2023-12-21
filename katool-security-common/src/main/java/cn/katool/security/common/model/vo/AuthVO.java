package cn.katool.security.common.model.vo;


import cn.katool.security.common.utils.JSONUtils;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class AuthVO implements Serializable {
    /**
     * 
     */
    private String id;

    /**
     * 
     */
    private String fid;

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
    private List<String> authRole;

    /**
     * 
     */
    private String operKaSecurityUser;

    /**
     * 
     */
    private Boolean checkLogin;

    /**
     * 
     */
    private Boolean isDef;

    private Boolean isOpen;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public void setAuthRole(String authRoleListJson) {
        this.authRole= JSONUtils.getList(authRoleListJson);
    }
}