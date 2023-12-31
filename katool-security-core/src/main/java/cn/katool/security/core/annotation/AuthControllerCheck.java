package cn.katool.security.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthControllerCheck {
    /**
     * 拥有任意一个角色即可通过
     */
    String[] anyRole() default "";
    /**
     * 必须有某个角色
     *"由于大多数情况下只有一个角色权限，所以说感觉这个很鸡肋"
     * @return
     */
    @Deprecated
    String mustRole() default "";


    /**
     * 检查登录，不会检查权限
     */
    boolean onlyCheckLogin() default false;

    /**
     * 排除的方法
     * @return
     */
    String[] excludeMethods() default "";
}