package io.github.lofbat.flow.aspect;

import io.github.lofbat.flow.biz.DependenceBeanIntercept;
import io.github.lofbat.flow.biz.EntryBeanIntercept;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Created by geqi on 2019/5/28.
 */
@Configuration
@Aspect
public class BeanInterceptorConfiguration {

    private final Logger logger = LoggerFactory.getLogger(BeanInterceptorConfiguration.class);

    @Autowired
    DependenceBeanIntercept dependenceBeanIntercept;

    @Autowired
    EntryBeanIntercept entryBeanIntercept;

    @Pointcut("@within(org.springframework.web.bind.annotation.RestController)" +
            "|| @within(org.springframework.stereotype.Controller)" +
            "|| @within(org.apache.dubbo.config.annotation.Service)")
    public void executionEntryService(){}

    @Pointcut("execution(* *..*Client.*(..))" +
            "|| execution(* *..*Dao.*(..))" +
            "|| execution(* *..*DAO.*(..))")
    public void executionDependencyService(){}

    @Around("executionEntryService()")
    public void arroundEntryBean(ProceedingJoinPoint pjp){

        try{
            entryBeanIntercept.beginRecord(pjp);
            Object object = pjp.proceed();
            entryBeanIntercept.endRecord(object);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Around("executionDependencyService()")
    public void arroundDependenceBean(ProceedingJoinPoint pjp){
        try {
            String invokeNo = dependenceBeanIntercept.beginRecord(pjp);
            Object object = pjp.proceed();
            dependenceBeanIntercept.endRecord(invokeNo,object);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
