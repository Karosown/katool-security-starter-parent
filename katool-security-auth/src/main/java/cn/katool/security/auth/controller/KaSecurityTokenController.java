package cn.katool.security.auth.controller;
import cn.katool.security.auth.model.dto.token.TokenDeleteRequest;
import cn.katool.security.auth.model.dto.token.TokenLoginOrLogoutRequest;
import cn.katool.security.auth.model.dto.token.TokenAddOrUpdRequest;
import cn.katool.security.core.model.entity.UserAgentInfo;

import cn.katool.security.auth.model.dto.token.TokenQueryRequest;
import cn.katool.security.auth.model.entity.KaSecurityUser;
import cn.katool.security.auth.utils.BaseResponse;
import cn.katool.security.auth.utils.ResultUtils;
import cn.katool.security.core.model.entity.TokenStatus;
import cn.katool.security.starter.utils.KaSecurityAuthUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import javafx.util.Pair;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/token")
public class KaSecurityTokenController extends KaSecurityAuthUtil<KaSecurityUser>{

    // 查询
    @GetMapping("/list")
    public BaseResponse list(){
        List<Map.Entry<String, TokenStatus>> tokenStatusList = this.getTokenStatusAllList();
        List<Pair> collect = tokenStatusList.stream().map(v -> {
            String token = v.getKey();
            TokenStatus tokenStatus = v.getValue();
            return new Pair(token, tokenStatus);
        }).collect(Collectors.toList());
        return ResultUtils.success(collect);
    }

    @GetMapping("/page")
    public BaseResponse<Page<Pair>> page(TokenQueryRequest queryRequest){
        List<Map.Entry<String, TokenStatus>> tokenStatusList = this.getTokenStatusAllList();
        String token = queryRequest.getToken();
        Integer status = queryRequest.getStatus();
        String primary = queryRequest.getPrimary();
        this.doCleanExpireToken();
        List<Map.Entry<String, TokenStatus>> res = tokenStatusList.stream().filter(v->{
            String cntPrimary = v.getValue().getPrimary();
            if (null == primary || "".equals(primary) || cntPrimary.equals(primary)) {
                return true;
            }
            return false;
        }).filter(v -> {
            String cntToken = v.getKey();
            if (null == token || "".equals(token) || cntToken.equals(token) || cntToken.contains(token)) {
                return true;
            }
            return false;
        }).filter(v -> {
            if (null == status || v.getValue().getStatus().equals(status)) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        res.sort(Comparator.comparing(o -> o.getValue().getPrimary()));
        long current = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        List<Map.Entry<String, TokenStatus>> child = res.subList((int) ((int) (current - 1) * pageSize), (int) (current * pageSize<res.size()?current*pageSize:res.size()));
        List<Pair> collect = child.stream().map(v -> {
            String tokene = v.getKey();
            TokenStatus tokenStatus = v.getValue();
            return new Pair(tokene, tokenStatus);
        }).collect(Collectors.toList());
        Page<Pair> ress = new Page<>(current, pageSize, res.size());
        ress.setRecords(collect);
        return ResultUtils.success(ress);
    }

    // 新增
    @PostMapping
    public BaseResponse<Boolean> add(@RequestBody TokenAddOrUpdRequest queryRequest){
     String token = queryRequest.getToken();
     UserAgentInfo ua_info = queryRequest.getUa_info();
     String primary = queryRequest.getPrimary();
     Integer status = queryRequest.getStatus();
     if (ObjectUtils.isEmpty(ua_info)){
         ua_info=this.getUserAgent();
     }
     TokenStatus tokenStatus = new TokenStatus();
     tokenStatus.setStatus(status);
     tokenStatus.setUa_info(ua_info);
     tokenStatus.setPrimary(primary);
    Boolean login = this.login(token, tokenStatus);
    return ResultUtils.success(login);
    }
    // 修改
    @PutMapping
    public BaseResponse<Boolean> update(@RequestBody TokenAddOrUpdRequest queryRequest) {
        String token = queryRequest.getToken();
        UserAgentInfo ua_info = queryRequest.getUa_info();
        String primary = queryRequest.getPrimary();
        Integer status = queryRequest.getStatus();
        if (ObjectUtils.isEmpty(ua_info)) {
            ua_info = this.getUserAgent();
        }
        TokenStatus tokenStatus = new TokenStatus();
        tokenStatus.setStatus(status);
        tokenStatus.setUa_info(ua_info);
        tokenStatus.setPrimary(primary);
        Boolean login = this.login(token, tokenStatus);
        return ResultUtils.success(login);
    }
    // 删除/登出
    @DeleteMapping
    public BaseResponse<Boolean> deleteOrLogOut(@RequestBody TokenDeleteRequest queryRequest){
     String primary = queryRequest.getPrimary();
     String token = queryRequest.getToken();
     this.logout(primary,token);
     return ResultUtils.success(true);
    }
    // 登录
    @PostMapping("/login")
    public BaseResponse<Boolean> login(@RequestBody TokenLoginOrLogoutRequest queryRequest) {
        String token = queryRequest.getToken();
        String primary = queryRequest.getPrimary();
        Boolean login = this.login(primary, token);
        return ResultUtils.success(login);
    }
    // 强制下线
    @PostMapping("/kickout")
    public BaseResponse<Boolean> kickout(@RequestBody TokenLoginOrLogoutRequest queryRequest) {
        String token = queryRequest.getToken();
        String primary = queryRequest.getPrimary();
        Boolean logout = this.kickout(primary, token);
        return ResultUtils.success(logout);
    }


}
