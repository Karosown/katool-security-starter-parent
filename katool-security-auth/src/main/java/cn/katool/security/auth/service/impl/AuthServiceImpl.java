package cn.katool.security.auth.service.impl;

import cn.katool.security.auth.model.entity.Auth;
import cn.katool.security.auth.service.AuthInnerService;
import cn.katool.security.core.annotation.AuthCheck;
import cn.katool.security.core.model.dto.auth.AuthAddRequest;
import cn.katool.security.core.model.dto.auth.AuthUpdateRequest;
import cn.katool.security.core.model.vo.AuthVO;
import cn.katool.security.service.AuthService;
import cn.katool.util.database.nosql.RedisUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.List;
import java.util.stream.Collectors;

@DubboService
public class AuthServiceImpl implements AuthService {
    @Resource
    AuthInnerService authInnerService;
    @Override
    public Boolean insert(AuthAddRequest addRequest) {
        return authInnerService.insert(addRequest);
    }

    @Override
    public Boolean change(AuthUpdateRequest authUpdateRequest) {
        return authInnerService.change(authUpdateRequest);
    }

    @Override
    public Boolean open(String method, String uri, String route) {
        return  authInnerService.open(method, uri, route);
    }

    @Override
    public Boolean open(String id) {
        return  authInnerService.open(id);
    }

    @Override
    public Boolean isOpen(String method, String uri, String route) {
        return  authInnerService.isOpen(method, uri, route);
    }

    @Override
    public Boolean reload() {
        return   authInnerService.reload();
    }

    @Override
    public Boolean open(List<String> ids) {
        return  authInnerService.open(ids);
    }

    @Override
    public Boolean close(List<String> ids) {
        return   authInnerService.close(ids);
    }

    @Override
    public Boolean close(String id) {
        return    authInnerService.close(id);
    }

    @Override
    public Boolean close(String method, String uri, String route) {
        return   authInnerService.close(method, uri, route);
    }

    @Override
    public AuthVO getOne(String method, String requestURI, String contextPath) {
        AuthVO authVO = new AuthVO();
        Auth one = authInnerService.getOne(method, requestURI, contextPath);
        if (ObjectUtils.isEmpty(one)){
            return null;
        }
        BeanUtils.copyProperties(one, authVO);
        return authVO;
    }

    @Override
    public List<AuthVO> getlistByIsOpen() {
        return   authInnerService.getlistByIsOpen().stream().map(v->{
            AuthVO authVO = new AuthVO();
            BeanUtils.copyProperties(v, authVO);
            return authVO;
        }).collect(Collectors.toList());
    }

    @Override
    public Boolean saveOrUpdate(AuthVO one) {
        Auth auth = new Auth();
        BeanUtils.copyProperties(one, auth);
        return    authInnerService.saveOrUpdate(auth);
    }
}
