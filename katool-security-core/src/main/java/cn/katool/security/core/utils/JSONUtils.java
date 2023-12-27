package cn.katool.security.core.utils;
/**
 * Title
 *
 * @ClassName: utils.cn.yb.thinktank.common.JSONUtils
 * @Description:
 * @author: 巫宗霖
 * @date: 2023/4/9 22:19
 * @Blog: https://www.wzl1.top/
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class JSONUtils {

    private static final Gson gson=new Gson();

    public static String getJSON(Object obj){
        return gson.toJson(obj);
    }

    public static List<String> getList(String json){
        return gson.fromJson(json,new TypeToken<List<String>>(){}.getType());
    }
}
