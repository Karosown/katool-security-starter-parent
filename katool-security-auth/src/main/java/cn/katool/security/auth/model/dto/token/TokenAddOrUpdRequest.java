package cn.katool.security.auth.model.dto.token;

import cn.katool.security.auth.model.dto.PageRequest;
import cn.katool.security.core.constant.KaSecurityConstant;
import cn.katool.security.core.model.entity.UserAgentInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TokenAddOrUpdRequest{
    @NotBlank
    String token;

    UserAgentInfo ua_info;

    @NotBlank
    String primary;
    Integer status= KaSecurityConstant.USER_ONLINE;
}
