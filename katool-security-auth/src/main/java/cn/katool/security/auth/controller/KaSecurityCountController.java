package cn.katool.security.auth.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.katool.security.auth.constant.DateUnit;
import cn.katool.security.auth.exception.BusinessException;
import cn.katool.security.auth.exception.ErrorCode;
import cn.katool.security.auth.model.dto.IncGraphRequest;
import cn.katool.security.auth.model.graph.IncGraph;
import cn.katool.security.auth.model.graph.IncGraphNode;
import cn.katool.security.auth.service.AuthInnerService;
import cn.katool.security.auth.service.KaSecurityUserService;

import cn.katool.security.auth.utils.BaseResponse;
import cn.katool.security.auth.utils.ResultUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.lang.management.ManagementFactory;
import java.util.List;

@Tag(name = "站点计数接口")
@RestController
@RequestMapping("/api/count")
public class KaSecurityCountController {

 @Resource
 KaSecurityUserService userService;
 @Resource
 AuthInnerService documentService;

    @Operation(description =  "获取用户计数")
    @GetMapping("/user")
    public BaseResponse<Long> getUserCount() {
        return ResultUtils.success(userService.count());
    }

    @Operation(description =  "获取接口计数")
    @GetMapping("/auth")
    public BaseResponse<Long> getDocumentCount() {
        return ResultUtils.success(documentService.count());
    }

    @Operation(description =  "运行时间计算")
    @GetMapping("/running")
    public BaseResponse<Long> getRunningCount() {
        return ResultUtils.success(ManagementFactory.getRuntimeMXBean().getStartTime());
    }
    @Operation(description =  "获取接口增长统计 -- Echarts")
    @GetMapping("/authinc")
    public BaseResponse<IncGraph> getGraphIncDataWithDoc(IncGraphRequest incGraphRequest) {
     Integer day = incGraphRequest.getDay();
     Integer week = incGraphRequest.getWeek();
     Integer month = incGraphRequest.getMonth();
     Integer year = incGraphRequest.getYear();
        if (ObjectUtil.isAllEmpty(day, week,month, year)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不完整");
        }
        List<IncGraphNode> graphIncData=null;
    if (day!=null){
         graphIncData= documentService.getGraphIncData(day, DateUnit.DAYS);
    }
    if (week!=null){
         graphIncData= documentService.getGraphIncData(week, DateUnit.WEEKS);
    }
    if (month!=null){
         graphIncData= documentService.getGraphIncData(month, DateUnit.MONTHS);
    }
    if (year!=null){
         graphIncData= documentService.getGraphIncData(year, DateUnit.YEARS);
    }
        IncGraph result = new IncGraph();
        graphIncData.forEach(v->{
            result.getColumns().add(v.getColumn());
            result.getValues().add(v.getValue());
     });
        return ResultUtils.success(result);
    }

    @Operation(description =  "获取用户增长统计 -- Echarts")
    @GetMapping("/userinc")
    public BaseResponse<IncGraph> getGraphIncDataWithUser(IncGraphRequest incGraphRequest) {
        Integer day = incGraphRequest.getDay();
        Integer week = incGraphRequest.getWeek();
        Integer month = incGraphRequest.getMonth();
        Integer year = incGraphRequest.getYear();
        if (ObjectUtil.isAllEmpty(day, week,month, year)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数不完整");
        }
        List<IncGraphNode> graphIncData=null;
        if (day!=null){
            graphIncData= userService.getGraphIncData(day, DateUnit.DAYS);
        }
        if (week!=null){
            graphIncData= userService.getGraphIncData(week, DateUnit.WEEKS);
        }
        if (month!=null){
            graphIncData= userService.getGraphIncData(month, DateUnit.MONTHS);
        }
        if (year!=null){
            graphIncData= userService.getGraphIncData(year, DateUnit.YEARS);
        }
        IncGraph result = new IncGraph();
        graphIncData.forEach(v->{
            result.getColumns().add(v.getColumn());
            result.getValues().add(v.getValue());
        });
        return ResultUtils.success(result);
    }
}
