package cn.katool.security.core.logic;

import cn.katool.security.core.config.KaSecurityCoreConfig;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class KaToolSecurityAuthQueue {

    private static final LinkedBlockingQueue<KaSecurityAuthLogic> list=new LinkedBlockingQueue<>();

    /**
     * 添加到末尾
     * @param logic
     */
    public static void add(KaSecurityAuthLogic logic){
        if (ObjectUtils.isEmpty(logic)){
            throw new IllegalArgumentException("logic is null");
        }
        list.add(logic);
    }

    /**
     * 便于修改逻辑顺序
     * @param posation
     * @param logic
     */
    public static void insert(Integer posation, KaSecurityAuthLogic logic){
        if (ObjectUtils.isEmpty(logic)){
            throw new IllegalArgumentException("logic is null.");
        }
        if (posation> list.size()){
            posation = list.size();
        }
        if (posation<0){
            throw new IllegalArgumentException(posation+"is out range.");
        }
        List<KaSecurityAuthLogic> collect = list.stream().collect(Collectors.toList());
        collect.add(posation,logic);
        list.clear();
        list.addAll(collect);
    }

    public static KaSecurityAuthLogic poll(){
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

    public static KaSecurityValidMessage run(List<String> roleList,List<String> permissionCodeList,Boolean onlyCheckLogin){
        for (KaSecurityAuthLogic logic : list) {
            KaSecurityValidMessage runResult = KaSecurityAuthLogic.ValidFilter(logic, roleList,permissionCodeList, onlyCheckLogin);
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

    public static void setQueue(LinkedBlockingQueue<KaSecurityAuthLogic> queue){
        KaToolSecurityAuthQueue.clear();
        list.addAll(queue);
    }

    public static LinkedBlockingQueue<KaSecurityAuthLogic> getList(){
        return list;
    }
}
