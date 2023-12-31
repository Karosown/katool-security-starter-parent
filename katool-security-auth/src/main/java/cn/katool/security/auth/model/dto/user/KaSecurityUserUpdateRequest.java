package cn.katool.security.auth.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class KaSecurityUserUpdateRequest {

    private Integer id;

    private String userName;

    private String passWord;

    private String userRole;
}
