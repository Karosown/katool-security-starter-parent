package cn.katool.security.task;

import cn.katool.config.cache.CacheConfig;
import cn.katool.security.core.config.KaSecurityCorePluginConfig;
import cn.katool.security.core.logic.KaSecurityAuthLogic;
import cn.katool.security.core.logic.KaToolSecurityAuthQueue;
import cn.katool.util.cache.utils.CaffeineUtils;
import cn.katool.util.classes.ClassUtil;
import cn.katool.util.classes.SpringContextUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.C;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AuthPluginLoaderTask {

    static CaffeineUtils<String,Object> flagBook = new CaffeineUtils<String,Object>(Caffeine.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .maximumSize(10)
            .build());

    @Resource
    KaSecurityCorePluginConfig config;
    @Resource
    ClassUtil classUtil;

    boolean listEq(List<String> list1, List<String> list2){
        if (ObjectUtils.isEmpty(list1)&&ObjectUtils.isEmpty(list2)){
            return true;
        }
        if (ObjectUtils.isEmpty(list1)||ObjectUtils.isEmpty(list2)||list1.size()!=list2.size()){
            return false;
        }
        List<Boolean> collect = list1.stream().map(v -> list2.contains(v)).collect(Collectors.toList());
        if (collect.contains(false)){
            return false;
        }
        return true;
    }
    boolean valid(){
        Boolean oldEnable  = (Boolean) flagBook.getIfNotExist("enable",false);
        String oldpackageName = (String) flagBook.getIfNotExist("packageName","");
        List<String> oldClassUrls = (List<String>) flagBook.getIfNotExist("classUrls",new ArrayList<>());
        if (oldEnable.equals(config.getEnable()) &&
                StringUtils.equals(config.getPackageName(),oldpackageName) &&
                listEq(oldClassUrls,config.getClassUrls())){
            return false;
        }
        return true;
    }
//    @Scheduled(fixedRate = 3 * 60 * 1000)
    public void initLoad(){
        if (!valid()){
            return ;
        }
        if (BooleanUtils.isTrue(config.getEnable()) && ObjectUtils.isEmpty(config.getClassUrls())){
            List<String> oldClassUrls = (List<String>) flagBook.getIfNotExist("classUrls", new ArrayList<String>());
            // 取出差集，避免加载短时间内已经加载过的类
            List<String> reduceList = config.getClassUrls().stream().filter(v -> !oldClassUrls.contains(v)).collect(Collectors.toList());
            KaToolSecurityAuthQueue.clear();
            reduceList.forEach(classUrl->{
                String className=classUrl.substring(classUrl.lastIndexOf('/') + 1, classUrl.lastIndexOf(".class"));
//                Class aClass = classUtil.urlLoader(classUrl, config.getPackageName()+"."+className);
                KaSecurityAuthLogic logic;
//                try {
//                    logic = (KaSecurityAuthLogic) aClass.newInstance();
//                } catch (InstantiationException e) {
//                    throw new RuntimeException(e);
//                } catch (IllegalAccessException e) {
//                    throw new RuntimeException(e);
//                }
                // 这个方法会自动重载已有的Bean
//                SpringContextUtils.regBean(className,logic);
            });
        }
        // 处理完之后，我们重新更新缓存
        flagBook.put("enable",config.getEnable()!=null? config.getEnable():false);
        flagBook.put("packageName",config.getPackageName()!=null?config.getPackageName():"");
        flagBook.put("classUrls",config.getClassUrls()!=null?config.getClassUrls():new ArrayList<String>());
    }
}
