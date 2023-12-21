package cn.katool.security.common.logic;

import org.springframework.util.ObjectUtils;

import java.util.concurrent.LinkedBlockingQueue;

public class KaAuthList {

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

    public static void run(){
        list.forEach(KaSecurityAuthLogic::doAuth);
    }

}
