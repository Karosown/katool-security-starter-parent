package cn.katool.security.common.logic;

import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class KaToolSecurityAuthQueue {

    private static LinkedBlockingQueue<KaSecurityAuthLogic> list=new LinkedBlockingQueue<>();

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

    public static Boolean run(List<String> authList){
        for (KaSecurityAuthLogic logic : list) {
            Boolean runResult = logic.doAuth(authList);
            if (!runResult) {
                return runResult;
            }
        }
        return true;
    }

}
