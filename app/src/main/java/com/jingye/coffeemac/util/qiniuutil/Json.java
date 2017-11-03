package com.jingye.coffeemac.util.qiniuutil;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by Hades on 2017/4/6.
 */
public class Json {
    private Json(){

    }

    public static String encode(StringMap map){
        return JSON.toJSONString(map.map());
//        return new Gson().toJson(map.map());
    }

    public static <T> T decode(String json,Class<T> classOfT){
        return JSON.parseObject(json,classOfT);
//        return new Gson().fromJson(json, classOfT);
    }

//    public static StringMap decode(String json){
//        Type type = new TypeToken<Map<String,Object>>(){
//        }.getType();
//        Map<String,Object> x=new Gson().fromJson(json,type);
//        return new StringMap(x);
//    }
}
