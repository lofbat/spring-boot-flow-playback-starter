package io.github.lofbat.flow.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

/**
 * Created by geqi on 2019/5/28.
 */
@Configuration
@Aspect
public class BeanInterceptorConfiguration {

    @Pointcut("@target(org.springframework.web.bind.annotation.RestController)" +
            "|| @target(org.springframework.stereotype.Controller)" +
            "|| @target(org.apache.dubbo.config.annotation.Service)")
    public void executionEntryService(){}

    @Pointcut("execution(* *..*Client.*(..))" +
            "|| execution(* *..*Dao.*(..))" +
            "|| execution(* *..*DAO.*(..))")
    public void executionDependencyService(){}

    @Around("executionEntryService()")
    public Object arround(ProceedingJoinPoint pjp){

        try{
            Object[] args = pjp.getArgs();
            args[0]="String";
            Object object = pjp.proceed(args);
            return object;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }

    @Around("executionDependencyService()")
    public void arround1(){

    }
}
