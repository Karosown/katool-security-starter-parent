package cn.katool.security.core.constant;
public interface KaSecurityConstant {
/**
 * 通用常量
 *
*/

    /**
     * 升序
     */
    String SORT_ORDER_ASC = "ascend";

    /**
     * 降序
     */
    String SORT_ORDER_DESC = " descend";

    public static String CACHE_LOGIN_TOKEN="KaSecurity:Login:token";

    public static Integer USER_ONLINE = 1;
    public static Integer USER_OFFLINE = 0;
    
}
