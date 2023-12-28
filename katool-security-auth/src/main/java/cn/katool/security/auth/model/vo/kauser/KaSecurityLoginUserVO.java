package cn.katool.security.auth.model.vo.kauser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class KaSecurityLoginUserVO {


    private String userName;




    private String userRole;



    private Date createdTime;


    private Date updateTime;
}
