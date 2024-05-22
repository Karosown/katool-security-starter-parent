package cn.katool.security.auth.model.dto.role;

import lombok.Data;

import java.io.Serializable;

@Data
public class KaSecurityRoleAddRequest implements Serializable {


    private Integer fId;


    private String userRole;


    private static final long serialVersionUID = 1L;


}