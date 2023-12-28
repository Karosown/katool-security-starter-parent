package cn.katool.security.auth.service;

import cn.katool.security.auth.model.entity.KaSecurityLoginLog;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author 30398
* @description 针对表【login_log(登录日志表)】的数据库操作Service
* @createDate 2023-04-07 17:32:01
*/
public interface KaSecurityLoginLogService extends IService<KaSecurityLoginLog> {

    List<KaSecurityLoginLog> listOrderByLoginTime(String userName);

    Long countByUserName(String userName);

    Page<KaSecurityLoginLog> getKaSecurityLoginLogPage(String userName, int page, int pageSize);
}
