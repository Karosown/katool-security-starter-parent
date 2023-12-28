package cn.katool.security.core.model.dto.auth;

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
    private String operUser;

    /**
     * 
     */
    private Boolean onlyCheckLogin;


    private Boolean isDef;


    private static final long serialVersionUID = 1L;
}