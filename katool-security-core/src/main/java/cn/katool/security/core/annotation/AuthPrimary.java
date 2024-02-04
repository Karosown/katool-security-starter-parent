package cn.katool.security.core.annotation;

import lombok.Data;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthPrimary  {
}
