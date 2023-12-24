package cn.katool.security.starter.gatway.utils;


import cn.katool.security.starter.gatway.core.constant.KaToolSecurityErrorCode;

/**
 * 返回工具类
 *
*/
public class  KaToolSecurityResultUtils {

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T>  KaToolSecurityBaseResponse<T> success(T data) {
        return new  KaToolSecurityBaseResponse<>(0, data, "ok");
    }

    /**
     * 成功
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T>  KaToolSecurityBaseResponse<T> success(T data,String message) {
        return new  KaToolSecurityBaseResponse<>(0, data, message);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static KaToolSecurityBaseResponse error(KaToolSecurityErrorCode errorCode) {
        return new  KaToolSecurityBaseResponse<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code
     * @param message
     * @return
     */
    public static KaToolSecurityBaseResponse error(int code, String message) {
        return new  KaToolSecurityBaseResponse(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode
     * @return
     */
    public static KaToolSecurityBaseResponse error(KaToolSecurityErrorCode errorCode, String message) {
        return new  KaToolSecurityBaseResponse(errorCode.getCode(), null, message);
    }
}
