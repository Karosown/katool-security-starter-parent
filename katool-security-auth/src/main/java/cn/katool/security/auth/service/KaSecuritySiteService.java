package cn.katool.security.auth.service;


import cn.katool.security.auth.model.entity.KaSecuritySite;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 30398
* @description 针对表【common(普通配置表)】的数据库操作Service
* @createDate 2023-01-17 07:53:55
*/
public interface KaSecuritySiteService extends IService<KaSecuritySite> {

    List<KaSecuritySite> querygetList();

    String getValue(String culomn);

    /**
     *  👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆👆
     *         正确示范
     *
     *         错误示范： 石山代码
     *  👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇👇


    String getSmsMail();

    Integer getSmsPort();

    String getSmsPassword();

    String getSmsTemplate();

    String getSmsTitle();

    String getHost();

     * @return
     */
}
