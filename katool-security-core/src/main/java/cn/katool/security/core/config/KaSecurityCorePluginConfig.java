package cn.katool.security.core.config;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.katool.Exception.ErrorCode;
import cn.katool.Exception.KaToolException;
import cn.katool.security.core.logic.KaSecurityAuthLogic;
import cn.katool.security.core.logic.KaToolSecurityAuthQueue;
import cn.katool.util.cache.utils.CaffeineUtils;
import cn.katool.util.classes.ClassUtil;
import cn.katool.util.classes.SpringContextUtils;
import com.alibaba.excel.util.ListUtils;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.qiniu.util.UrlUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.spring.util.SpringCompatUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.ui.context.support.UiApplicationContextUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Data
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@RefreshScope
@Component("KaSecurityConfig-PLUGIN")
@ConfigurationProperties("katool.security.plugin")
public class KaSecurityCorePluginConfig {
    /**
     * 是否开启插件化插入（插件化控制 > 配置类控制）
     */
     Boolean enable = false;

     List<String> classUrls = new ArrayList<String>();

     String packageName = "";

    static CaffeineUtils<String,Object> flagBook = new CaffeineUtils<String,Object>(Caffeine.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .maximumSize(10)
            .build());

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
        if (oldEnable.equals(this.getEnable()) &&
                StringUtils.equals(this.getPackageName(),oldpackageName) &&
                listEq(oldClassUrls,this.getClassUrls())){
            return false;
        }
        return true;
    }
    @Scheduled(fixedRate = 3 * 60 * 1000)
    public void initLoad(){
        if (!valid()){
            return ;
        }

        if (BooleanUtils.isTrue(this.getEnable()) && ObjectUtils.isNotEmpty(this.getClassUrls())){
            List<String> oldClassUrls = (List<String>) flagBook.getIfNotExist("classUrls", new ArrayList<String>());
            // 取出差集，避免加载短时间内已经加载过的类
            List<String> reduceList = this.getClassUrls().stream().filter(v -> !oldClassUrls.contains(v)).collect(Collectors.toList());
            List<KaSecurityAuthLogic> logicList = new ArrayList<>();
            reduceList.forEach(classUrl->{
                try {
                    classUrl = URLDecoder.decode(classUrl, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                String className=classUrl.substring(classUrl.lastIndexOf('/') + 1, classUrl.lastIndexOf(".class"));
                if (StringUtils.isBlank(className)){
                    throw new KaToolException(ErrorCode.PARAMS_ERROR,"Class文件名不符法，请使用.class最为后缀，同时保证文件名是类名");
                }
                Class aClass = classUtil.urlLoader(classUrl,this.getPackageName(),className);
                KaSecurityAuthLogic logic;
                try {
                    logic = (KaSecurityAuthLogic) aClass.newInstance();
                } catch (InstantiationException e) {
                    throw new RuntimeException(e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                logicList.add(logic);
                SpringContextUtils.regBeanNotAutoWire(className,logic);
            });
            List<String> innerList = this.getClassUrls().stream().filter(v -> oldClassUrls.contains(v)).collect(Collectors.toList());
            innerList.forEach(classUrl->{
                try {
                    classUrl = URLDecoder.decode(classUrl, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                String className=classUrl.substring(classUrl.lastIndexOf('/') + 1, classUrl.lastIndexOf(".class"));
                if (StringUtils.isBlank(className)){
                    throw new KaToolException(ErrorCode.PARAMS_ERROR,"Class文件名不符法，请使用.class最为后缀，同时保证文件名是类名");
                }
                KaSecurityAuthLogic logic = (KaSecurityAuthLogic) SpringContextUtils.getBean(className);
                logic.loadPlugin();
            });
            clearOldBean();
            // 统一处理，避免异常。
            log.info("正在对鉴权逻辑进行替换");
            KaToolSecurityAuthQueue.clear();
            logicList.forEach(KaSecurityAuthLogic::loadPlugin);

        }
        // 处理完之后，我们重新更新缓存
        flagBook.put("enable",this.getEnable()!=null? this.getEnable():false);
        flagBook.put("packageName",this.getPackageName()!=null?this.getPackageName():"");
        flagBook.put("classUrls",this.getClassUrls()!=null?this.getClassUrls():new ArrayList<String>());
    }

    private void clearOldBean() {
        String oldClassUrls = (String) flagBook.getIfNotExist("classUrls", new ArrayList<String>());
        List<String> reduceList = this.getClassUrls().stream().filter(v -> !oldClassUrls.contains(v)).collect(Collectors.toList());
        reduceList.forEach(classUrl -> {
            try {
                classUrl = URLDecoder.decode(classUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

            String className=classUrl.substring(classUrl.lastIndexOf('/') + 1, classUrl.lastIndexOf(".class"));
            if (StringUtils.isBlank(className)){
                throw new KaToolException(ErrorCode.PARAMS_ERROR,"Class文件名不符法，请使用.class最为后缀，同时保证文件名是类名");
            }
            SpringContextUtils.unregBean(className);
        });
    }

}
