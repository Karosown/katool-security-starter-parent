/**
 * Title
 *
 * @ClassName: AuthController
 * @Description:
 * @author: 巫宗霖
 * @date: 2023/5/27 12:03
 * @Blog: https://www.wzl1.top/
 */

package cn.katool.security.auth.controller;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.katool.security.auth.exception.BusinessException;
import cn.katool.security.auth.exception.ErrorCode;
import cn.katool.security.auth.utils.BaseResponse;
import cn.katool.security.auth.utils.ResultUtils;
import cn.katool.security.common.annotation.AuthCheck;
import cn.katool.security.common.constant.CommonConstant;
import cn.katool.security.common.model.dto.auth.*;
import cn.katool.security.common.model.entity.Auth;
import cn.katool.security.common.model.vo.AuthVO;
import cn.katool.security.service.AuthService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Tag(name = "鉴权服务")
@RestController
@RequestMapping
public class AuthController {
    @Resource
    AuthService authService;

    @Operation(description = "权限刷新总接口")
    @GetMapping("/reload")
    @Transactional
    public BaseResponse<Boolean> reload(){
        return ResultUtils.success(authService.reload());
    }

    @Operation(description = "路由权限列表分页查询获取")
    @GetMapping("/page")
    public BaseResponse<IPage<AuthVO>> getPage(AuthQueryRequest authQueryRequest){

     String fid = authQueryRequest.getFid();
     String method = authQueryRequest.getMethod();
     String uri = authQueryRequest.getUri();
     String route = authQueryRequest.getRoute();
     List<String> authRole = authQueryRequest.getAuthRole();
     String operKaSecurityUser = authQueryRequest.getOperKaSecurityUser();
     Boolean checkLogin = authQueryRequest.getCheckLogin();
     Boolean isDef = authQueryRequest.getIsDef();
     long current = authQueryRequest.getCurrent();
     long pageSize = authQueryRequest.getPageSize();
     String sortField = authQueryRequest.getSortField();
     String sortOrder = authQueryRequest.getSortOrder();
     Page<Auth> authPage = new Page<>(current,pageSize);
        QueryWrapper<Auth> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(fid)) {
            queryWrapper.eq("fid",fid);
        }
        if (StringUtils.isNotBlank(method)) {
            queryWrapper.eq("method",method);
        }
        if (StringUtils.isNotBlank(uri)) {
            queryWrapper.eq("uri",uri);
        }
        if (StringUtils.isNotBlank(route)) {
            queryWrapper.eq("route",route);
        }
        if (authRole!=null && StringUtils.isNoneEmpty((String[])authRole.stream().toArray())) {

            final Integer[] flag = {0};

            authRole.forEach(v->{
                if (StringUtils.isNotBlank(v)) {
                    queryWrapper.or(flag[0] !=0).eq("auth_role",v);
                    flag[0]++;
                }

            });
        }
        if (StringUtils.isNotBlank(operKaSecurityUser)) {
                queryWrapper.like("oper_KaSecurityUser",operKaSecurityUser);
        }
        queryWrapper.orderBy(true, CommonConstant.SORT_ORDER_ASC.equals(sortOrder),sortField)
                .eq(BooleanUtils.isTrue(checkLogin),"check_login",checkLogin)
                .eq(BooleanUtils.isTrue(isDef),"is_def",isDef);
        authService.page(authPage, queryWrapper);
        IPage<AuthVO> convert = authPage.convert(v -> {
            AuthVO authVO = new AuthVO();
            BeanUtils.copyProperties(v, authVO);
            return authVO;
        });
        return ResultUtils.success(convert);
    }


    @Operation(description = "路由权限列表分页查询获取")
    @GetMapping("/list")
    public BaseResponse<IPage<AuthVO>> getlist(AuthQueryRequest authQueryRequest){

        Page<Auth> page = new Page<>();
        page.setCurrent(authQueryRequest.getCurrent()).setSize(authQueryRequest.getPageSize());
        page = authService.page(page);
        IPage<AuthVO> convert = page.convert(v -> {
            AuthVO authVO = new AuthVO();
            BeanUtils.copyProperties(v, authVO);
            return authVO;
        });
        log.info("{}",convert.getRecords());
        return ResultUtils.success(convert);
    }
    @Operation(description = "开启接口鉴权")
    @PutMapping("/open")
    public BaseResponse<Boolean> open(@RequestBody AuthOperRequest authOperRequest){
        if (ObjectUtils.isEmpty(authOperRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String id = authOperRequest.getId();
        Boolean open=false;
        if (StringUtils.isNotBlank(id)){
            open = authService.open(id);
        }
        String[] ids = authOperRequest.getIds();
        if (ObjectUtils.isNotEmpty(ids)){
             open = authService.open(ListUtil.toList(ids));
        }
        return ResultUtils.success(BooleanUtils.isTrue(open));
    }

    @Operation(description = "关闭接口鉴权")
    @PutMapping("/close")
    public BaseResponse<Boolean> close(@RequestBody AuthOperRequest authOperRequest){
        if (ObjectUtils.isEmpty(authOperRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String id = authOperRequest.getId();
        Boolean close=false;
        if (StringUtils.isNotBlank(id)){
            close = authService.close(id);
        }
        String[] ids = authOperRequest.getIds();
        if (ObjectUtil.isNotEmpty(ids)){
            close = authService.close(ListUtil.toList(ids));
        }
        return ResultUtils.success(BooleanUtils.isTrue(close));
    }

    @Operation(description = "修改鉴权接口")
    @PostMapping("/")
    public BaseResponse<AuthVO> insert(@RequestBody AuthAddRequest authAddRequest){
        Boolean insert = authService.insert(authAddRequest);
        if (ObjectUtil.isEmpty(insert)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        if (insert){
            QueryWrapper<Auth> query=new QueryWrapper();
            query.eq("method",authAddRequest.getMethod())
                    .eq("route",authAddRequest.getRoute())
                    .eq("uri",authAddRequest.getUri());
            Auth one = authService.getOne(query);
            AuthVO target = new AuthVO();
            BeanUtils.copyProperties(one, target);
            return ResultUtils.success(target);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR);
    }

    @Operation(description = "删除鉴权接口")
    @DeleteMapping("/")
    @AuthCheck(anyRole = "admin")
    public BaseResponse<Boolean> delete(@RequestBody AuthDeleteRequest deleteRequest) {
        // id 或者 ids 都为空
        if (ObjectUtil.isAllEmpty(deleteRequest.getId(), deleteRequest.getIds())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Auth> query = new QueryWrapper();
        // 不为空的就删除
        if (deleteRequest.getId()!=null){
            query.eq("id",deleteRequest.getId());
        }
        if (deleteRequest.getIds()!=null){
            query.in("id",deleteRequest.getIds());
        }
        // 删除

        Boolean delete = authService.remove(query);
        return ResultUtils.success(delete);
    }

    @Operation(description = "批量删除鉴权接口")
    @DeleteMapping("/batch")
    @AuthCheck(anyRole = "admin")
    public BaseResponse<Boolean> deleteBatch(@RequestBody AuthDeleteRequest deleteRequest) {
        // id 或者 ids 都为空
        if (ObjectUtil.isAllEmpty(deleteRequest.getId(), deleteRequest.getIds())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Auth> query = new QueryWrapper();
        // 不为空的就删除
        if (deleteRequest.getId()!=null){
            query.eq("id",deleteRequest.getId());
        }
        if (deleteRequest.getIds()!=null){
            query.in("id",deleteRequest.getIds());
        }
        // 删除

        Boolean delete = authService.remove(query);
        return ResultUtils.success(delete);
    }

    @Operation(description = "修改鉴权接口")
    @PutMapping("/")
    public BaseResponse<AuthVO> update(@RequestBody AuthUpdateRequest updateRequest){
        Auth auth = new Auth();
        BeanUtils.copyProperties(updateRequest,auth);
        boolean b = authService.updateById(auth);
        Auth byId = authService.getById(auth.getId());
        AuthVO target = new AuthVO();
        BeanUtils.copyProperties(byId, target);
        return ResultUtils.success(target);
    }
}
