package io.github.lofbat.flow.biz.intercept;

import io.github.lofbat.flow.model.bo.BeanInterceptBO;
import io.github.lofbat.flow.model.converter.BeanInterceptBOConverter;
import io.github.lofbat.flow.utils.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by geqi on 2019/5/29.
 */
@Component
@Slf4j
public class EntryBeanIntercept extends AbstractBeanIntercept {

    @Override
    public void record(ProceedingJoinPoint pjp) {

        beginRecord(pjp);
        Object object = null;
        try {
            object = pjp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        endRecord(object);
    }

    public void beginRecord(JoinPoint joinPoint){

        BeanInterceptBO beanInterceptBO = buildBeanInterceptBO(joinPoint);

        if(Objects.nonNull(beanInterceptBO)){
            beanInterceptBO.setUniqueNo(beanInterceptBO.getInvokeNo());
            beanInterceptBO.setType(1);
            beanInterceptBO.setSerialNo(0);

            setThreadInvokeSign(beanInterceptBO.getUniqueNo(),0);

            saveBeanInterceptBO(beanInterceptBO);
        }
    }

    public void endRecord(Object object){

        String invokeNo = getUniqueNo();
        removeThreadInvokeSign();

        BeanInterceptBO beanInterceptBO = BeanInterceptBO.builder()
                .invokeNo(invokeNo)
                .returnValue(SerializeUtil.serialize(object))
                .build();

        invokeDetailDAO.updateById(BeanInterceptBOConverter.toInvokeDetailDO(beanInterceptBO));
    }

}
