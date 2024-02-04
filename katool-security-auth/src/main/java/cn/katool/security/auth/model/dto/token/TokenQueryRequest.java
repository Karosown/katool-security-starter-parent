package cn.katool.security.auth.model.dto.token;

import cn.katool.security.auth.model.dto.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TokenQueryRequest extends PageRequest {
    String token;
    String primary;
    Integer status;
}
