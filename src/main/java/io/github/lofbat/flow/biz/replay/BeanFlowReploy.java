package io.github.lofbat.flow.biz.replay;

import io.github.lofbat.flow.utils.SerializeUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by geqi on 2019/6/3.
 */
abstract class BeanFlowReploy implements FlowReplay{

    private static ApplicationContext applicationContext;

    @PostConstruct
    private void init() {
        applicationContext = ContextLoader.getCurrentWebApplicationContext();
    }

    public static Object getBeanByClassName(String className){

        return applicationContext.getBean(className);
    }

    public static Object invoke(String className,String args,String methodName) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object object = getBeanByClassName(className);
        Class clazz = object.getClass();

        Method method;


        method = findMethod(clazz,methodName);


        Type[] types = method.getParameterTypes();

        Class[] classes = new Class[types.length];

        for(int i = 0,l=types.length;i<l;i++){
            classes[i]=types[0].getClass();
        }

        Object[] objects = SerializeUtil.deserializeArray(args);

        return method.invoke(object,objects);

    }

    private static Method findMethod(Class clazz,String method) throws NoSuchMethodException {
        Method[] methods = clazz.getMethods();
        for(Method m : methods){
            if(m.equals(method)){
                return m;
            }
        }
        throw new NoSuchMethodException(clazz.getName()+":"+method);
    }
}
