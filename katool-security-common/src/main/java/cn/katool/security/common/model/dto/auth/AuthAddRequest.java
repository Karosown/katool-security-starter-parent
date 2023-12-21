package cn.katool.security.common.model.dto.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @TableName auth
 */
@Data
@AllArgsConstructor
public class AuthAddRequest implements Serializable {

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
    @TableField(value = "is_def")
    private Boolean isDef;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}