/**
 * Title
 *
 * @ClassName: AuthTableSyncTask
 * @Description:
 * @author: Karos
 * @date: 2023/8/3 4:05
 * @Blog: https://www.wzl1.top/
 */

package cn.katool.security.auth.job;

import cn.katool.security.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class AuthTableSyncTask {
    @Resource
    AuthService authService;

    Integer runConst=0;
    //通过定时任务
    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void run() {

//         移动到鉴权中心进行定时任务
        if (this.runConst%10==0){
            // 每过10分钟进行一次全局清零，至少走一次本地服务
            // 这里解释一下
            // 有一种情况，针对某一类型的用户，进行某些特定接口的不定时开放和关闭
            authService.reload();
        }

    }

    @Scheduled(fixedRate = 10 * 60 * 1000)
    public void runn() {

//         移动到鉴权中心进行定时任务
        if (this.runConst%10==0){
            // 每过10分钟进行一次全局清零，至少走一次本地服务
            // 这里解释一下
            // 有一种情况，针对某一类型的用户，进行某些特定接口的不定时开放和关闭
            authService.reload();
        }

    }
}
