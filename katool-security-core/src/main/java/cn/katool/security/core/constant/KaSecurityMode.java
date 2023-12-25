package cn.katool.security.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KaSecurityMode {
    GATEWAY("gateway"),
    SINGLE("single"),
    ZUUL("zuul");

    private String mode;

}
