package cn.katool.security.auth.service.impl;

import cn.katool.security.auth.model.entity.Auth;
import cn.katool.security.auth.service.AuthServiceInterface;
import cn.katool.security.core.model.dto.auth.AuthAddRequest;
import cn.katool.security.core.model.dto.auth.AuthUpdateRequest;
import cn.katool.security.core.model.vo.AuthVO;
import cn.katool.security.service.AuthService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@DubboService
public class AuthServiceImpl implements AuthService {
    @Resource
    AuthServiceInterface authService;
    @Override
    public Boolean insert(AuthAddRequest addRequest) {
        return authService.insert(addRequest);
    }

    @Override
    public Boolean change(AuthUpdateRequest authUpdateRequest) {
        return authService.change(authUpdateRequest);
    }

    @Override
    public Boolean open(String method, String uri, String route) {
        return  authService.open(method, uri, route);
    }

    @Override
    public Boolean open(String id) {
        return  authService.open(id);
    }

    @Override
    public Boolean isOpen(String method, String uri, String route) {
        return  authService.isOpen(method, uri, route);
    }

    @Override
    public Boolean reload() {
        return   authService.reload();
    }

    @Override
    public Boolean open(List<String> ids) {
        return  authService.open(ids);
    }

    @Override
    public Boolean close(List<String> ids) {
        return   authService.close(ids);
    }

    @Override
    public Boolean close(String id) {
        return    authService.close(id);
    }

    @Override
    public Boolean close(String method, String uri, String route) {
        return   authService.close(method, uri, route);
    }

    @Override
    public AuthVO getOne(String method, String requestURI, String contextPath) {
        AuthVO authVO = new AuthVO();
        BeanUtils.copyProperties(authService.getOne(method, requestURI, contextPath), authVO);
        return authVO;
    }

    @Override
    public List<AuthVO> getlistByIsOpen() {
        return   authService.getlistByIsOpen().stream().map(v->{
            AuthVO authVO = new AuthVO();
            BeanUtils.copyProperties(v, authVO);
            return authVO;
        }).collect(Collectors.toList());
    }

    @Override
    public boolean saveOrUpdate(AuthVO one) {
        Auth auth = new Auth();
        BeanUtils.copyProperties(one, auth);
        return    authService.saveOrUpdate(auth);
    }
}
