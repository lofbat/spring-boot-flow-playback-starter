package io.github.lofbat.flow.utils;

import com.google.gson.Gson;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Created by geqi on 2019/6/3.
 */
public class SerializeUtilTest {

    private static Gson gson = new Gson();

    @BeforeMethod
    public void setUp() {
    }

    @Test
    public void t(){
        Integer[] ints = new Integer[]{1,2,3};
        String s = SerializeUtil.serializeArray(ints);
        System.out.println(s);
        Object[] objects = SerializeUtil.deserializeArray(s);
        for(Object o:objects){
            System.out.println(objects);
        }

        System.out.println(gson.fromJson("2", Integer.class));
    }

    @AfterMethod
    public void tearDown() {
    }
}