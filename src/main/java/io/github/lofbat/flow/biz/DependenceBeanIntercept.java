package io.github.lofbat.flow.biz;

import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

/**
 * Created by geqi on 2019/5/29.
 */
@Component
public class DependenceBeanIntercept extends AbstractBeanIntercept {

    public void record(JoinPoint joinPoint){

    }
}
