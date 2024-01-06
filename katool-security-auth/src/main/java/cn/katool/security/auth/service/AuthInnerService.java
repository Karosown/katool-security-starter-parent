package cn.katool.security.auth.service;

import cn.katool.security.auth.constant.DateUnit;
import cn.katool.security.auth.model.entity.Auth;
import cn.katool.security.auth.model.graph.IncGraphNode;
import cn.katool.security.core.model.dto.auth.AuthAddRequest;
import cn.katool.security.core.model.dto.auth.AuthUpdateRequest;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author 30398
 * @description 针对表【auth】的数据库操作Service
 * @createDate 2023-05-27 11:29:05
 */
public interface AuthInnerService extends IService<Auth> {

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

    Auth getOne(String method, String requestURI, String contextPath);

    List<Auth> getlistByIsOpen();

    List<IncGraphNode> getGraphIncData(Integer num, DateUnit dateUnit);
}
