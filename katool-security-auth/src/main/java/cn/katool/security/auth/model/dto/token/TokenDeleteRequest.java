package cn.katool.security.auth.model.dto.token;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TokenDeleteRequest {
    @NotBlank
    String primary;
    @NotBlank
    String token;
}
