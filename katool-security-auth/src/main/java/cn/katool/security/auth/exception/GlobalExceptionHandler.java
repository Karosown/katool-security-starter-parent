package cn.katool.security.auth.exception;

import cn.hutool.jwt.JWTException;

import cn.katool.security.auth.utils.BaseResponse;
import cn.katool.security.auth.utils.ResultUtils;
import cn.katool.util.database.nosql.RedisUtils;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 全局异常处理器
 *
*/
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("cn.katool.security.auth.exception.BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> throwCustomException(MethodArgumentNotValidException methodArgumentNotValidException){
        return ResultUtils.error(ErrorCode.PARAMS_ERROR,methodArgumentNotValidException.getMessage());
    }

    @ExceptionHandler(JWTException.class)
    public BaseResponse<?> JWTExceptionHandler(JWTException e) {
        log.error("JWTException", e);
        return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR, "未登录或登录状态失效，请重新登录！");
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
        log.error("RuntimeException", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
