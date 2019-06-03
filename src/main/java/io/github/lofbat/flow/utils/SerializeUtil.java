package io.github.lofbat.flow.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.*;

/**
 * Created by geqi on 2019/5/30.
 */
public class SerializeUtil {

    private static Gson gson = new Gson();

    public static String serialize(Object obj){
        return gson.toJson(obj);
    }

    public static <T> T deserialize(String json, Class<T> clazz){
        return gson.fromJson(json, clazz);
    }


    public static String serializeArray(Object[] objects){
        if(objects.length==0){
            return "";
        }
        ObjectPair[] objectPairs = new ObjectPair[objects.length];
        for(int i = 0;i<objects.length;i++){
            objectPairs[i]=new ObjectPair(objects[i].getClass().getName(),serialize(objects[i]));
        }
        return JSONArray.toJSONString(objectPairs);
    }

    @Deprecated
    public static Object[] deserializeArray(String json){

        List<Object> list = new LinkedList<>();
        JSONArray jsonArray = JSONArray.parseArray(json);

        Iterator ite = jsonArray.iterator();
        while(ite.hasNext()){
            JSONObject jo = (JSONObject)ite.next();
            try {
                list.add(deserialize(jo.getString("object"),Class.forName(jo.getString("name"))));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                list.add(null);
            }
        }

        return list.toArray();
    }

    public static Object[] deserializeArray(String json,Class[] classes){
        List<Object> list = new LinkedList<>();
        JSONArray jsonArray = JSONArray.parseArray(json);

        if(jsonArray.size()!=classes.length){
            throw new IllegalArgumentException("classes num not corroct");
        }
        Iterator ite = jsonArray.iterator();
        int i = 0;
        while(ite.hasNext()){
            JSONObject jo = (JSONObject)ite.next();
            list.add(deserialize(jo.getString("name"),classes[i++]));
        }

        return list.toArray();
    }

    @Data
    @ToString
    @AllArgsConstructor
    public static class ObjectPair{

        String name;

        String object;
    }

}
