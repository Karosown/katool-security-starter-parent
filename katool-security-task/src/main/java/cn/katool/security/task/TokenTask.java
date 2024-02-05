package cn.katool.security.task;

import cn.katool.security.core.model.entity.UserAgentInfo;
import cn.katool.security.starter.utils.AbstractKaSecurityAuthUtil;
import cn.katool.security.starter.utils.DefaultKaSecurityAuthUtilInterface;
import cn.katool.security.task.config.TokenCleanConfig;
import cn.katool.util.database.nosql.RedisUtils;
import jdk.nashorn.internal.parser.Token;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@EnableScheduling
public class TokenTask{

        private AbstractKaSecurityAuthUtil authUtil = new AbstractKaSecurityAuthUtil() {
            @Override
            public String getTokenWithHeader(String headerName) {
                return null;
            }

            @Override
            public String getTokenWithParameter(String parameterName) {
                return null;
            }

            @Override
            public String getTokenWithCookie(String cookieName) {
                return null;
            }
        };
    @Resource
    RedisUtils redisUtils;

    // 主要用于执行定期删除和过期删除策略
    @Scheduled(cron = "${katool.security.task.clean-token.corn:0 0 0/1 * * ?}")
    public void run(){
        boolean b = redisUtils.tryLock("token_clean_lock".intern());
        // 使用分布式锁保证集群条件下只有一个节点在执行清理操作
        if(b){
            authUtil.doCleanExpireToken(TokenCleanConfig.effort_config);
            redisUtils.unTryLock("token_clean_lock".intern());
        }
    }





}
