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
public class DependenceBeanIntercept extends AbstractBeanIntercept {

    @Override
    public void record(ProceedingJoinPoint pjp) {

        String invokeNo = beginRecord(pjp);

        Object object = null;

        try {
            object = pjp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        endRecord(invokeNo,object);
    }

    public String beginRecord(JoinPoint joinPoint){

        addThreadInvokeSignSerialNo();

        BeanInterceptBO beanInterceptBO = buildBeanInterceptBO(joinPoint);

        if(Objects.nonNull(beanInterceptBO)){
            beanInterceptBO.setType(1);
            beanInterceptBO.setUniqueNo(getUniqueNo());
            beanInterceptBO.setSerialNo(getSerialNo());

            saveBeanInterceptBO(beanInterceptBO);

            return beanInterceptBO.getInvokeNo();
        }

        return "";
    }

    public void endRecord(String invokeNo,Object object){

        BeanInterceptBO beanInterceptBO = BeanInterceptBO.builder()
                .invokeNo(invokeNo)
                .returnValue(SerializeUtil.serialize(object))
                .build();

        invokeDetailDAO.updateById(BeanInterceptBOConverter.toInvokeDetailDO(beanInterceptBO));
    }

}
