package cn.katool.security.common.model.dto.auth;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 删除请求
 *
*/
@Data
public class AuthDeleteRequest implements Serializable {


    private String id;


    private List<String> ids;

    private static final long serialVersionUID = 1L;
}