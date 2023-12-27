package cn.katool.security.starter.gateway.utils;

import cn.katool.security.starter.gateway.core.constant.KaToolSecurityErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 通用返回类
 *
 * @param <T>
*/
@Data
public class KaToolSecurityBaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public  KaToolSecurityBaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public  KaToolSecurityBaseResponse(int code, T data) {
        this(code, data, "");
    }

    public  KaToolSecurityBaseResponse(KaToolSecurityErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
