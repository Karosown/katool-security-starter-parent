/**
 * Title
 *
 * @ClassName: AuthServcie
 * @Description:
 * @author: Karos
 * @date: 2023/8/18 12:47
 * @Blog: https://www.wzl1.top/
 */

package cn.katool.security.core.annotation;

import cn.katool.security.core.constant.KaSecurityAuthCheckMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthServiceCheck {
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
    String[] mustRole() default "";
    

    /**
     * 检查登录，不会检查权限
     */
    boolean onlyCheckLogin() default false;
    /**
     * 该接口需要的权限码
     * 支持多个权限，只要有一个通过即可
     * @return
     */
    String[] anyPermissionCodes() default "";
    String[] mustPermissionCodes() default "";

    /**
     * 排除的方法
     * @return
     */
    String[] excludeMethods() default "";
    KaSecurityAuthCheckMode roleMode() default KaSecurityAuthCheckMode.AND;
    KaSecurityAuthCheckMode  permissionMode() default KaSecurityAuthCheckMode.AND;
    int[] logicIndex() default {0};

}
