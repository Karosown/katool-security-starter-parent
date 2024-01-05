package cn.katool.security.auth.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 普通配置表
 * @TableName common
 */
@Data
@EqualsAndHashCode
@TableName(value ="ka_security_site")
public class KaSecuritySite implements Serializable {
    /**
     * 属性
     */
    @TableId
    //@ApiModelProperty("属性名")
    private String attribute;

    /**
     * 值
     */
    //@ApiModelProperty("属性值")
    private String value;

    //@ApiModelProperty("属性备注")
    private String comment;

    //@ApiModelProperty("属性类型")
    private Integer type;
    private Date createdTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}