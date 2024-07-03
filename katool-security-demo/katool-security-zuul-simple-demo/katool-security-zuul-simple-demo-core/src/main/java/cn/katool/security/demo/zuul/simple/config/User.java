package cn.katool.security.demo.zuul.simple.config;

import cn.katool.security.core.annotation.AuthPrimary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @AuthPrimary
    String username;
    String password;

    List<String> userRoles;
    List<String> userPermissions;
}