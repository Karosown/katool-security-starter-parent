package cn.katool.security.auth.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录日志表
 * @TableName login_log
 */
@TableName(value ="login_log")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
public class KaSecurityLoginLog implements Serializable {
    /**
     * 登录日志ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 登录IP地址
     */
    @TableField(value = "ip")
    private String ip;

    /**
     * 登录账号
     */
    @TableField(value = "userName")
    private String userName;

    /**
     * 登录时间
     */
    @TableField(value = "loginTime")
    private Date loginTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}