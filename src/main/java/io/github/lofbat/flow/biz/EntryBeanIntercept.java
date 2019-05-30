package io.github.lofbat.flow.biz;

import io.github.lofbat.flow.model.BeanInterceptBO;
import io.github.lofbat.flow.model.BeanInterceptBOConverter;
import io.github.lofbat.flow.utils.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by geqi on 2019/5/29.
 */
@Component
@Slf4j
public class EntryBeanIntercept extends AbstractBeanIntercept {

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

        invokeDetailDAO.update(BeanInterceptBOConverter.toInvokeDetailDO(beanInterceptBO));
    }

}
