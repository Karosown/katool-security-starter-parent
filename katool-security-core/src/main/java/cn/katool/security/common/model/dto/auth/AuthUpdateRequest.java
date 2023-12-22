package cn.katool.security.common.model.dto.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @TableName auth
 */
@Data
@AllArgsConstructor
public class AuthUpdateRequest implements Serializable {

    @NotEmpty
    private String id;

    private String method;
    /**
     *
     */
    private String fid;

    /**
     *
     */
    private String uri;
    private Boolean isOpen;
    /**
     *
     */
    private String route;


    private List<String> authRole;

    /**
     *
     */
    private String operKaSecurityUser;

    /**
     *
     */
    private Boolean checkLogin;

    /**
     *
     */
    @TableField(value = "is_def")
    private Boolean isDef;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}