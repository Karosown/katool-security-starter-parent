package cn.katool.security.core.logic;

import cn.hutool.core.util.ObjectUtil;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class KaToolSecurityAuthQueue {

    private static final LinkedBlockingQueue<KaSecurityAuthLogic> list=new LinkedBlockingQueue<>();

    public static void add(KaSecurityAuthLogic logic){
        if (ObjectUtils.isEmpty(logic)){
            throw new IllegalArgumentException("logic is null");
        }
        list.add(logic);
    }

    public static KaSecurityAuthLogic get(){
        return list.poll();
    }

    public static void clear(){
        list.clear();
    }

    public static int size(){
        return list.size();
    }

    public static boolean isEmpty(){
        return list.isEmpty();
    }

    public static KaSecurityValidMessage run(List<String> roleList,Boolean onlyCheckLogin){
        for (KaSecurityAuthLogic logic : list) {
            KaSecurityValidMessage runResult = KaSecurityAuthLogic.ValidFilter(logic, roleList, onlyCheckLogin);
            // 如果返回结果是null，那么就是未知错误
            if (ObjectUtils.isEmpty(runResult)){
                runResult = KaSecurityValidMessage.unKnow();
            }
            if (!KaSecurityValidMessage.success().equals(runResult)||!KaSecurityValidMessage.onlyLogin().equals(runResult)) {
                return runResult;
            }
        }
        return KaSecurityValidMessage.success();
    }

}
