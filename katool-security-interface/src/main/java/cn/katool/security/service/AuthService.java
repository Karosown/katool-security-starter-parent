package cn.katool.security.service;

import cn.katool.security.core.model.dto.auth.AuthAddRequest;
import cn.katool.security.core.model.dto.auth.AuthUpdateRequest;
import cn.katool.security.core.model.vo.AuthVO;

import java.util.List;

/**
* @author 30398
* @description 针对表【auth】的数据库操作Service
* @createDate 2023-05-27 11:29:05
*/
public interface AuthService {

        Boolean insert(AuthAddRequest addRequest);
        Boolean change(AuthUpdateRequest authUpdateRequest);
        Boolean open(String method, String uri, String route);
        Boolean open(String id);
        Boolean isOpen(String method, String uri, String route);

        Boolean reload();
        Boolean open(List<String> ids);
        Boolean close(List<String> ids);

        Boolean close(String id);

        Boolean close(String method, String uri, String route);

    AuthVO getOne(String method, String requestURI, String contextPath);

    List<AuthVO> getlistByIsOpen();

        Boolean saveOrUpdate(AuthVO one);
}
