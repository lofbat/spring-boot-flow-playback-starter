package io.github.lofbat.flow.biz;

import io.github.lofbat.flow.model.BeanInterceptBO;
import io.github.lofbat.flow.model.BeanInterceptBOConverter;
import io.github.lofbat.flow.utils.SerializeHelper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Created by geqi on 2019/5/29.
 */
@Component
@Slf4j
public class DependenceBeanIntercept extends AbstractBeanIntercept {

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
                .returnValue(SerializeHelper.serialize(object))
                .build();

        invokeDetailDAO.update(BeanInterceptBOConverter.toInvokeDetailDO(beanInterceptBO));
    }

}
