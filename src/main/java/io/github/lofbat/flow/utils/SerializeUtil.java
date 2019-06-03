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

    public static Object deserialize(String json, Class<?> clazz){
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

    public static Object[] deserializeArray(String json){
        List<Object> list = new LinkedList<>();
        JSONObject jsonObject = JSONArray.parseObject(json);
        JSONArray jsonArray = jsonObject.getJSONArray("ObjectPair");
        Iterator ite = jsonArray.iterator();
        while(ite.hasNext()){
            JSONObject jo = (JSONObject)ite.next();
            try {
                list.add(deserialize(jo.getString("className"),Class.forName(jo.getString("object"))));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                list.add(null);
            }
        }

        return list.toArray();
    }



    @Data
    @ToString
    @AllArgsConstructor
    private static class ObjectPair{

        String className;

        String object;
    }

}
