package cn.katool.security.auth.model.vo.kauser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class KaSecurityUserVO implements Serializable {
    private Integer id;

    private String userName;

    private String userRole;

    private Date createdTime;

    private Date updateTime;
    private static final long serialVersionUID = 1L;
}
