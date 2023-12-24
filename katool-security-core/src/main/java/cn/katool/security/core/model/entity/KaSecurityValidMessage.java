package cn.katool.security.core.model.entity;

import lombok.*;
import lombok.experimental.Accessors;

@Data
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class KaSecurityValidMessage {
    private static final Integer SUNKNOW_CODE = 0;
    private static final Integer UNAUTH_CODE = 403;
    private static final Integer UNLOGIN_CODE = 401;
    private static final Integer UNKNOW_CODE = 404;
    private static final KaSecurityValidMessage SUCCESS_MESSAGE = KaSecurityValidMessage.builder().code(SUNKNOW_CODE).message("success").build();
    private static final KaSecurityValidMessage NOAUTH_MESSAGE = KaSecurityValidMessage.builder().code(UNAUTH_CODE).message("NO AUTH").build();
    private static final KaSecurityValidMessage UNLOGIN_MESSAGE = KaSecurityValidMessage.builder().code(UNLOGIN_CODE).message("UN LOGIN").build();
    private static final KaSecurityValidMessage UNKNOW_MESSAGE = KaSecurityValidMessage.builder().code(UNKNOW_CODE).message("UNKNOW ERROR").build();


    Integer code;
    String message;

    public static KaSecurityValidMessage success(){
        return  SUCCESS_MESSAGE;
    }
    public static KaSecurityValidMessage noAuth(){
        return  NOAUTH_MESSAGE;
    }
    public static KaSecurityValidMessage unLogin(){
        return  UNLOGIN_MESSAGE;
    }
    public static KaSecurityValidMessage unKnow(){
        return  UNKNOW_MESSAGE;
    }

}
