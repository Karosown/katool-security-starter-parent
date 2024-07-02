package cn.katool.security.core.model.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @TableName auth
 */
@Data
@AllArgsConstructor
public class AuthUpdateRequest implements Serializable {

    @NotEmpty
    private String id;

    private String method;
    /**
     *
     */


    /**
     *
     */
    private String uri;
    private Boolean isOpen;
    /**
     *
     */
    private String route;


    private List<String> anyRole;

    private List<String> mustRole;

    private List<String> anyPermission;
    private List<String> mustPermission;
    private List<Integer> logicIndex;
    private Integer roleMode;
    private Integer permissionMode;
    /**
     *
     */
    private String operUser;
    private String serviceName;

    /**
     *
     */
    private Boolean onlyCheckLogin;

    /**
     *
     */
    private Boolean isDef;

    private static final long serialVersionUID = 1L;
}