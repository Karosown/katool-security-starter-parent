package cn.katool.security.auth.service.impl;


import cn.katool.security.auth.exception.BusinessException;
import cn.katool.security.auth.exception.ErrorCode;
import cn.katool.security.auth.mapper.KaSecuritySiteMapper;
import cn.katool.security.auth.model.entity.KaSecuritySite;
import cn.katool.security.auth.service.KaSecuritySiteService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.List;

/**
* @author 30398
* @description 针对表【common(普通配置表)】的数据库操作Service实现
* @createDate 2023-01-17 07:53:55
*/
@DubboService
public class KaSecuritySiteServiceImpl extends ServiceImpl<KaSecuritySiteMapper, KaSecuritySite>
    implements KaSecuritySiteService {


    @Override
    public List<KaSecuritySite> querygetList() {
        QueryWrapper<KaSecuritySite> queryWrapper=new QueryWrapper<>();
        queryWrapper.orderByAsc("createdTime");
        List<KaSecuritySite> list = this.list(queryWrapper);
        return list;
    }

    @Override
    public String getValue(String culomn) {
        LambdaQueryWrapper<KaSecuritySite> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(KaSecuritySite::getAttribute,culomn);
        KaSecuritySite common = this.getOne(lambdaQueryWrapper);
        if(common==null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"请检查配置表中是否存在"+culomn+"配置");
        }
        return common.getValue();
    }

}




