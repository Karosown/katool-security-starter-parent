/**
 * Title
 *
 * @ClassName: KaSecuritySiteController
 * @Description:
 * @author: Karos
 * @date: 2023/1/17 8:40
 * @Blog: https://www.wzl1.top/
 */

package cn.katool.security.auth.controller;



import cn.katool.security.auth.constant.KaSecurityUserConstant;
import cn.katool.security.auth.model.dto.DeleteRequest;
import cn.katool.security.auth.model.dto.site.KaSecuritySiteSaveRequest;
import cn.katool.security.auth.model.entity.KaSecuritySite;
import cn.katool.security.auth.service.KaSecuritySiteService;
import cn.katool.security.auth.utils.BaseResponse;
import cn.katool.security.auth.utils.ResultUtils;
import cn.katool.security.core.annotation.AuthCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Tag(name = "站点信息配置服务")
@RestController("KaSecurityAuthController")
@RequestMapping("/api/site")
public class KaSecuritySiteController {
    @Resource
    KaSecuritySiteService kaSecuritySiteService;

    //@ApiOperationSupport(author = "wzl")
    //@ApiOperation(value = "获得属性列表接口")
    @Operation(description = "站点属性列表获取接口")
    @GetMapping("/attribute/list")
    public BaseResponse<List<String>> getAttributelist(){

        List<KaSecuritySite> list = kaSecuritySiteService.list();
        return ResultUtils.success(list.stream().map(v->{
            String s=v.getAttribute();
            return s;
        }).collect(Collectors.toList()));
    }

    //@ApiOperationSupport(author = "wzl")
    //@ApiOperation(value = "获得值列表接口")
    @Operation(description = "站点属性值获取接口")
    @GetMapping("/value/list")
    @AuthCheck(anyRole = {KaSecurityUserConstant.ADMIN_ROLE})
    public BaseResponse<List<String>> getValuelist(){
        List<KaSecuritySite> list = kaSecuritySiteService.list();
        return ResultUtils.success(list.stream().map(v->{
            String s=v.getValue();
//            Integer type = v.getType();
//            if (type==2){  // 2为密码，不回显，保证信息安全
//                return null;
//            }
            return s;
        }).collect(Collectors.toList()));
    }

    //@ApiOperationSupport(author = "wzl")
    //@ApiOperation(value = "获得所有所有属性和其值")
    @Operation(description = "站点属性字典获取接口")
    @GetMapping("/list")
    @AuthCheck(anyRole = {KaSecurityUserConstant.ADMIN_ROLE})
    public BaseResponse<List<KaSecuritySite>> getlist(){
        List<KaSecuritySite> list = kaSecuritySiteService.querygetList();
        return ResultUtils.success(list);
    }

    //@ApiOperationSupport(author = "wzl")
    //@ApiOperation(value = "保存所有所有属性和其值")
    @Operation(description = "站点属性保存接口")
    @PostMapping("/save")
    @AuthCheck(anyRole = {KaSecurityUserConstant.ADMIN_ROLE})
    public BaseResponse<Boolean> save(@RequestBody KaSecuritySiteSaveRequest kaSecuritySiteSaveRequest){
        boolean b = kaSecuritySiteService.saveOrUpdateBatch(kaSecuritySiteSaveRequest.getKaSecuritySiteList());
        return ResultUtils.success(b);
    }

    //@ApiOperationSupport(author = "wzl")
    //@ApiOperation(value = "删除某些属性",notes = "支持批量删除，请求体内传入id或ids")
    @Operation(description = "站点属性删除接口")
    @PostMapping("/delete")
    @AuthCheck(anyRole = {KaSecurityUserConstant.ADMIN_ROLE})
    public BaseResponse<Boolean> delete(@RequestBody DeleteRequest deleteRequest){
        boolean b = kaSecuritySiteService.removeByIds(deleteRequest.getIds());
        return ResultUtils.success(b);
    }

    //@ApiOperationSupport(author = "wzl")
    //@ApiOperation(value = "获得某个属性的值")
    @Operation(description = "站点属性获取接口")
    @GetMapping("/get")
    public BaseResponse<String> getAttribute(@RequestParam("attribute") String attribute){
        KaSecuritySite byId = kaSecuritySiteService.getById(attribute);
        return ResultUtils.success(byId!=null?byId.getType()==2?null:byId.getValue():null);
    }
}
