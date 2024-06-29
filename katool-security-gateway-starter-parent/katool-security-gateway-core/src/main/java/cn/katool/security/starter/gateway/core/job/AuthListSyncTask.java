/**
 * Title
 *
 * @ClassName: AuthListSyncTask
 * @Description:
 * @author: Karos
 * @date: 2023/5/27 21:01
 * @Blog: https://www.wzl1.top/
 */

package cn.katool.security.starter.gateway.core.job;


import cn.katool.security.core.model.vo.AuthVO;
import cn.katool.security.starter.gateway.core.constant.GlobalContainer;
import cn.katool.security.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Component
public class AuthListSyncTask {
    @Resource
    AuthService authService;


    //通过定时任务
    @Scheduled(fixedRate = 60 * 1000)
    public void run() {
        log.info("[网关鉴权名单同步任务开始执行]AuthListSyncTask run");
        GlobalContainer.authRouteList.removeAllElements(); // 删除所有元素，保证数据一致性
        List<AuthVO> list = authService.getlistByIsOpen();
        list.forEach(v->{
            GlobalContainer.Route e = new GlobalContainer.Route(v.getMethod(), v.getUri(), v.getRoute(), v.getAuthRoles(),v.getPermissionCodes())
                    .setCheckLogin(v.getOnlyCheckLogin())
                    .setOpen(v.getIsOpen())
                    .setDef(v.getIsDef())
                    .setRole(v.getAuthRoles())
                    .addRole(v.getAuthRole())
                    .setRole(v.getPermissionCodes());
            log.info("open the route:{}",e);
            GlobalContainer.authRouteList.add(
                    e
            );
        });
        log.info("[网关鉴权名单同步任务执行结束]AuthListSyncTask end");
    }
}
