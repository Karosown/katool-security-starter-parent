package cn.katool.security.auth.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class KaSecurityUserUpdatePassWordRequest  implements Serializable {
    String passWord;
    String uuid;
    private static final long serialVersionUID = 1L;
}