package cn.katool.security.logic;

import cn.katool.security.core.constant.KaSecurityAuthCheckMode;
import cn.katool.security.core.model.entity.KaSecurityValidMessage;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class KaToolSecurityAuthQueue {

    private static final CopyOnWriteArrayList<KaSecurityAuthLogic> list=new CopyOnWriteArrayList<>();

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
    public static KaSecurityAuthLogic get(Integer pos){
        if (pos>=list.size()){
            throw new IllegalArgumentException("pos is out of range");
        }
        if (pos<0){
            pos = list.size()+pos;
        }
        return list.get(pos);
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
            posation = list.size() + posation;
        }
        list.add(posation,logic);
    }

    public static KaSecurityAuthLogic poll(){
        KaSecurityAuthLogic kaSecurityAuthLogic = list.get(0);
        list.remove(0);
        return kaSecurityAuthLogic;
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


    private static KaSecurityValidMessage run(List<String> anyRoleList, List<String> mustRoleList,
                                             List<String> anyPermissionCodeList, List<String> mustPermissionCodeList,
                                             Boolean onlyCheckLogin, KaSecurityAuthCheckMode roleMode, KaSecurityAuthCheckMode permissionMode){
        for (KaSecurityAuthLogic logic : list) {
            KaSecurityValidMessage runResult = KaSecurityAuthLogic.ValidFilter(logic,anyRoleList,mustRoleList,
                    anyPermissionCodeList,mustPermissionCodeList,onlyCheckLogin,roleMode,permissionMode);
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
    public static KaSecurityValidMessage run(List<String> anyRoleList, List<String> mustRoleList,
                                             List<String> anyPermissionCodeList, List<String> mustPermissionCodeList,
                                             Boolean onlyCheckLogin, KaSecurityAuthCheckMode roleMode, KaSecurityAuthCheckMode permissionMode,List<Integer> logicIndex){
        if (logicIndex.contains(Integer.MAX_VALUE)){
            return run(anyRoleList, mustRoleList, anyPermissionCodeList, mustPermissionCodeList, onlyCheckLogin, roleMode, permissionMode);
        }

       for(Integer index:logicIndex ){
           KaSecurityAuthLogic logic = list.get(index);
           KaSecurityValidMessage runResult = KaSecurityAuthLogic.ValidFilter(logic,anyRoleList,mustRoleList,
                    anyPermissionCodeList,mustPermissionCodeList,onlyCheckLogin,roleMode,permissionMode);
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

    public static void setQueue(CopyOnWriteArrayList<KaSecurityAuthLogic> queue){
        KaToolSecurityAuthQueue.clear();
        list.addAll(queue);
    }

    public static CopyOnWriteArrayList<KaSecurityAuthLogic> getList(){
        return list;
    }
}
