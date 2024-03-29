package cn.katool.security.core.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TokenStatus {

    String primary;
    UserAgentInfo ua_info;

    Integer status;
}
