package io.github.lofbat.flow.aspect;

import io.github.lofbat.flow.biz.intercept.DependenceBeanIntercept;
import io.github.lofbat.flow.biz.intercept.EntryBeanIntercept;
import io.github.lofbat.flow.config.InterceptConfig;
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

    @Autowired
    InterceptConfig interceptConfig;

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
        if(interceptConfig.getStart()){
            entryBeanIntercept.record(pjp);
            return;
        }
        try {
            pjp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Around("executionDependencyService()")
    public void arroundDependenceBean(ProceedingJoinPoint pjp){
        if(interceptConfig.getStart()){
            dependenceBeanIntercept.record(pjp);
            return;
        }
        try {
            pjp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
