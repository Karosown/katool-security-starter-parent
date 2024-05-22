package cn.katool.security.auth.model.dto.role;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
public class KaSecurityRoleUpdateRequest implements Serializable {

    private Integer id;


    private Integer fId;


    private String userRole;


    private static final long serialVersionUID = 1L;


}