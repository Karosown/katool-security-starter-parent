package cn.katool.security.auth.model.dto.user;

import cn.katool.security.auth.model.dto.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class KaSecurityUserQueryRequest extends PageRequest implements Serializable {

    private Integer id;

    private String userName;

    private String passWord;

    private String userRole;

    private static final long serialVersionUID = 1L;

}
