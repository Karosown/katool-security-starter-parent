package cn.katool.security.task;

import cn.katool.security.core.model.entity.UserAgentInfo;
import cn.katool.security.starter.utils.DefaultKaSecurityAuthUtilInterface;
import cn.katool.security.task.config.TokenCleanConfig;
import jdk.nashorn.internal.parser.Token;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@EnableScheduling
public class TokenTask implements DefaultKaSecurityAuthUtilInterface {



    // 主要用于执行定期删除和过期删除策略
    @Scheduled(cron = "${katool.security.task.clean-token.corn:0 0 0/1 0 0 ? *}")
    public void run(){
        this.doCleanExpireToken(TokenCleanConfig.effort_config);
    }




















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

    @Override
    public UserAgentInfo getUserAgent() {
        return null;
    }

    @Override
    public String login(Object payload) {
        return null;
    }
}
