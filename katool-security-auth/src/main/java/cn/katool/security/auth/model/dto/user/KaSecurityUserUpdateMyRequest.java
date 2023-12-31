package cn.katool.security.auth.model.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class KaSecurityUserUpdateMyRequest implements Serializable {

    private String userName;


    private static final long serialVersionUID = 1L;
}