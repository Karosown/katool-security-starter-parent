package cn.katool.security.auth.model.entity;

import cn.katool.security.core.annotation.AuthCheck;
import cn.katool.security.core.annotation.AuthPrimary;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName ka_security_user
 */
@TableName(value ="ka_security_user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class KaSecurityUser implements Serializable {
    /**
     *
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    @TableField(value = "user_name")
    @AuthPrimary
    private String userName;

    /**
     *
     */

    /**
     * 
     */
    @TableField(value = "pass_word")
    private String passWord;

    /**
     * 
     */
    @TableField(value = "user_role")
    private String userRole;

    /**
     * 
     */
    @TableLogic(delval = "1", value = "0")
    @TableField(value = "is_delete")
    private Integer isDelete;

    /**
     * 
     */
    @TableField(value = "created_time")
    private Date createdTime;

    /**
     * 
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}