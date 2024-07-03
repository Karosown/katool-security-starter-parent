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

    private String method;
    /**
     * 
     */
    private String uri;

    /**
     * 
     */
    private String route;


    private List<String> anyRole;

    private List<String> mustRole;

    private List<String> anyPermission;
    private List<String> mustPermission;
    private Integer roleMode;
    private Integer permissionMode;
    private List<Integer> logicIndex;

    private String operUser;
    /**
     *
     */
    private Boolean onlyCheckLogin;

    private String serviceName;



    private Boolean isDef;


    private static final long serialVersionUID = 1L;
}