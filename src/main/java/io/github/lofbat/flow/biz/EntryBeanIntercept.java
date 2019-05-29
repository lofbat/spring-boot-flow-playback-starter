package io.github.lofbat.flow.biz;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

/**
 * Created by geqi on 2019/5/29.
 */
@Component
@Slf4j
public class EntryBeanIntercept extends AbstractBeanIntercept {

    public void beginRecord(JoinPoint joinPoint){
        log.info("begin record ");

    }

    public void endRecord(Object object){

    }
}
