package cn.katool.security.auth.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Builder
public class KaSecurityUser {
    private String userName;
    private String passWord;
}
