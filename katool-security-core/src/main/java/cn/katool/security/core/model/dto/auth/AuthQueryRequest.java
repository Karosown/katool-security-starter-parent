package cn.katool.security.core.model.dto.auth; /**
 * Title
 *
 * @ClassName: auditor.dto.model.cn.katool.security.auth.AuditorQueryRequest
 * @Description:
 * @author: Karos
 * @date: 2023/4/6 23:06
 * @Blog: https://www.wzl1.top/
 */



import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class AuthQueryRequest extends AuthPageRequest implements Serializable {



    private String method;
    /**
     *
     */
    private String uri;

    /**
     *
     */
    private String route;


    private List<String> authRole;

    /**
     *
     */
    private String operUser;

    private String serviceName;
    /**
     *
     */
    private Boolean onlyCheckLogin;

    /**
     *
     */
    private Boolean isDef;

    private static final long serialVersionUID = 1L;
}
