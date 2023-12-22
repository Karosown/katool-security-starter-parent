package cn.katool.security.interceptor;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ResponseData {
    int code;
    Object data;
    String message;
}
