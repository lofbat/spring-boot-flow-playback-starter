package io.github.lofbat.flow.utils;

import com.google.gson.Gson;

/**
 * Created by geqi on 2019/5/30.
 */
public class SerializeHelper {

    private static Gson gson = new Gson();

    public static String serialize(Object obj){
        return gson.toJson(obj);
    }

    public static Object deserialize(String json, Class<?> clazz){
        return gson.fromJson(json, clazz);
    }

}
