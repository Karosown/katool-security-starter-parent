package cn.katool.security.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KaSecurityMode {
    GATEWAY("gateway"),
    SINGLE("single");

    private String mode;

}
