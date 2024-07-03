/**
 * Title
 *
 * @ClassName: AuthController
 * @Description:
 * @author: Karos
 * @date: 2023/5/27 12:03
 * @Blog: https://www.wzl1.top/
 */

package cn.katool.security.auth.controller;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.katool.security.auth.exception.BusinessException;
import cn.katool.security.auth.exception.ErrorCode;
import cn.katool.security.auth.service.AuthInnerService;
import cn.katool.security.auth.utils.BaseResponse;
import cn.katool.security.auth.utils.ResultUtils;
import cn.katool.security.core.annotation.AuthCheck;
import cn.katool.security.core.constant.KaSecurityConstant;
import cn.katool.security.core.model.dto.auth.*;
import cn.katool.security.auth.model.entity.Auth;
import cn.katool.security.core.model.vo.AuthVO;
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
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Tag(name = "鉴权服务")
@RestController
@RequestMapping("/api")
public class KaSecurityAuthController {
    @Resource
    AuthInnerService authInnerService;

    @Operation(summary = "权限刷新总接口",
            description = "用于权限刷新")
    @Transactional
    @GetMapping("/reload")
    public BaseResponse<Boolean> reload(){
        return ResultUtils.success(authInnerService.reload());
    }

    @Operation(summary = "路由分页查询",description = "路由权限列表分页查询获取")
    @GetMapping("/page")
    public BaseResponse<IPage<AuthVO>> getPage(AuthQueryRequest authQueryRequest){
     String method = authQueryRequest.getMethod();
     String uri = authQueryRequest.getUri();
     String route = authQueryRequest.getRoute();
     String serviceName = authQueryRequest.getServiceName();
     List<String> authRole = authQueryRequest.getAuthRole();
     String operUser = authQueryRequest.getOperUser();
     Boolean onlyCheckLogin = authQueryRequest.getOnlyCheckLogin();
     Boolean isDef = authQueryRequest.getIsDef();
     long current = authQueryRequest.getCurrent();
     long pageSize = authQueryRequest.getPageSize();
     String sortField = authQueryRequest.getSortField();
     String sortOrder = authQueryRequest.getSortOrder();
     Page<Auth> authPage = new Page<>(current,pageSize);
    QueryWrapper<Auth> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(method)) {
            queryWrapper.eq("method",method);
        }
        if (StringUtils.isNotBlank(uri)) {
            queryWrapper.eq("uri",uri);
        }
        if (StringUtils.isNotBlank(route)) {
            queryWrapper.eq("route",route);
        }
        if (authRole!=null && !authRole.isEmpty() && StringUtils.isNoneEmpty((String[])authRole.stream().toArray())) {

            final Integer[] flag = {0};

            authRole.forEach(v->{
                if (StringUtils.isNotBlank(v)) {
                    queryWrapper.or(flag[0] !=0).eq("auth_role",v);
                    flag[0]++;
                }

            });
        }
        if (StringUtils.isNotBlank(operUser)) {
                queryWrapper.like("oper_user",operUser);
        }
        queryWrapper.orderBy(StringUtils.isNotBlank(sortField), KaSecurityConstant.SORT_ORDER_ASC.equals(sortOrder),sortField)
                .eq(BooleanUtils.isTrue(onlyCheckLogin),"check_login",onlyCheckLogin)
                .eq(BooleanUtils.isTrue(isDef),"is_def",isDef);
        authInnerService.page(authPage, queryWrapper);
        IPage<AuthVO> convert = authPage.convert(v -> {
            AuthVO authVO = new AuthVO();
            BeanUtils.copyProperties(v, authVO);
            return authVO;
        });
        return ResultUtils.success(convert);
    }


    @Operation(summary = "路由分页获取",description = "路由权限列表分页查询获取")
    @GetMapping("/list")
    public BaseResponse<IPage<AuthVO>> getlist(AuthQueryRequest authQueryRequest){

        Page<Auth> page = new Page<>();
        page.setCurrent(authQueryRequest.getCurrent()).setSize(authQueryRequest.getPageSize());
        page = authInnerService.page(page);
        IPage<AuthVO> convert = page.convert(v -> {
            AuthVO authVO = new AuthVO();
            BeanUtils.copyProperties(v, authVO);
            authVO.setLogicIndexs(v.getLogicIndexs()).setAnyRole(v.getAnyRoles())
                    .setMustRole(v.getMustRoles()).setAnyPermission(v.getAnyPermissions())
                    .setMustPermission(v.getMustPermissions())
                    .setPermissionMode(v.getPermissionMode())
                    .setRoleMode(v.getRoleMode());
            return authVO;
        });
        log.info("{}",convert.getRecords());
        return ResultUtils.success(convert);
    }
    @Operation(summary = "开启接口鉴权",description = "开启接口鉴权")
    @PutMapping("/open")
    public BaseResponse<Boolean> open(@RequestBody AuthOperRequest authOperRequest){
        if (ObjectUtils.isEmpty(authOperRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String id = authOperRequest.getId();
        Boolean open=false;
        if (StringUtils.isNotBlank(id)){
            open = authInnerService.open(id);
        }
        String[] ids = authOperRequest.getIds();
        if (ObjectUtils.isNotEmpty(ids)){
             open = authInnerService.open(ListUtil.toList(ids));
        }
        return ResultUtils.success(BooleanUtils.isTrue(open));
    }

    @Operation(summary = "关闭接口鉴权",description = "关闭接口鉴权")
    @PutMapping("/close")
    public BaseResponse<Boolean> close(@RequestBody AuthOperRequest authOperRequest){
        if (ObjectUtils.isEmpty(authOperRequest)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String id = authOperRequest.getId();
        Boolean close=false;
        if (StringUtils.isNotBlank(id)){
            close = authInnerService.close(id);
        }
        String[] ids = authOperRequest.getIds();
        if (ObjectUtil.isNotEmpty(ids)){
            close = authInnerService.close(ListUtil.toList(ids));
        }
        return ResultUtils.success(BooleanUtils.isTrue(close));
    }

    @Operation(summary = "新增鉴权路由",description = "新增鉴权接口")
    @PostMapping("/")
    public BaseResponse<AuthVO> insert(@RequestBody AuthAddRequest authAddRequest){
        Boolean insert = authInnerService.insert(authAddRequest);
        if (ObjectUtil.isEmpty(insert)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        if (insert){
            QueryWrapper<Auth> query=new QueryWrapper();
            query.eq("method",authAddRequest.getMethod())
                    .eq("route",authAddRequest.getRoute())
                    .eq("uri",authAddRequest.getUri());
            Auth one = authInnerService.getOne(query);
            AuthVO target = new AuthVO();
            BeanUtils.copyProperties(one, target);
            return ResultUtils.success(target);
        }
        return ResultUtils.error(ErrorCode.OPERATION_ERROR);
    }

    @Operation(summary = "删除路由",description = "删除鉴权接口")
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

        Boolean delete = authInnerService.remove(query);
        return ResultUtils.success(delete);
    }

    @Operation(summary = "批量删除路由",description = "批量删除鉴权接口")
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

        Boolean delete = authInnerService.remove(query);
        return ResultUtils.success(delete);
    }

    @Operation(summary = "修改路由",description = "修改鉴权接口")
    @PutMapping("/")
    public BaseResponse<AuthVO> update(@RequestBody AuthUpdateRequest updateRequest){
        Auth auth = new Auth();
        BeanUtils.copyProperties(updateRequest,auth);
        boolean b = authInnerService.updateById(auth);
        Auth byId = authInnerService.getById(auth.getId());
        AuthVO target = new AuthVO();
        BeanUtils.copyProperties(byId, target);
        return ResultUtils.success(target);
    }
}
