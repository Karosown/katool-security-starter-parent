package cn.katool.security.auth.service.impl;

import cn.katool.security.auth.mapper.KaSecurityLoginLogMapper;
import cn.katool.security.auth.model.entity.KaSecurityLoginLog;

import cn.katool.security.auth.service.KaSecurityLoginLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
* @author 30398
* @description 针对表【login_log(登录日志表)】的数据库操作Service实现
* @createDate 2023-04-07 17:32:01
*/
@DubboService
public class KaSecurityLoginLogServiceImpl extends ServiceImpl<KaSecurityLoginLogMapper, KaSecurityLoginLog>
    implements KaSecurityLoginLogService {

    @Override
    public List<KaSecurityLoginLog> listOrderByLoginTime(String userName) {
        QueryWrapper<KaSecurityLoginLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName",userName)
       .orderBy(true,true,"loginTime");
        return this.list(queryWrapper);
    }

    @Override
    public Long countByUserName(String userName) {
        QueryWrapper<KaSecurityLoginLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userName",userName);
        return this.count(queryWrapper);
    }

    @Override
    public Page<KaSecurityLoginLog> getKaSecurityLoginLogPage(String userName, int page, int pageSize){
        //分页构造器
        Page<KaSecurityLoginLog> pageInfo = new Page<>(page,pageSize);

        //条件构造器
        LambdaQueryWrapper<KaSecurityLoginLog> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //添加过滤条件
        lambdaQueryWrapper.like(userName != null,KaSecurityLoginLog::getUserName,userName);

        //添加排序条件
        lambdaQueryWrapper.orderByDesc(KaSecurityLoginLog::getLoginTime);

        return this.page(pageInfo,lambdaQueryWrapper);
    }
}




