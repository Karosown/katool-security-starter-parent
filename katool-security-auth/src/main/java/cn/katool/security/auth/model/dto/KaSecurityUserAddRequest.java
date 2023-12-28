package cn.katool.security.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class KaSecurityUserAddRequest {

    private String userName;


    private String passWord;


    private String userRole;
}
