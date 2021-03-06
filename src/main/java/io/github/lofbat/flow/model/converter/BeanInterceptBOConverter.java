package io.github.lofbat.flow.model.converter;

import io.github.lofbat.flow.model.bo.BeanInterceptBO;
import io.github.lofbat.flow.model.dataobject.InvokeDetailDO;
import io.github.lofbat.flow.model.dataobject.InvokeInfoDO;

/**
 * Created by geqi on 2019/5/30.
 */
public class BeanInterceptBOConverter {

    public static InvokeInfoDO toInvokeInfoDO(BeanInterceptBO beanInterceptBO){

        return InvokeInfoDO.builder()
                .app(beanInterceptBO.getApp())
                .status(0)
                .type(beanInterceptBO.getType())
                .serialNo(beanInterceptBO.getSerialNo())
                .ext(beanInterceptBO.getExt())
                .invokeNo(beanInterceptBO.getInvokeNo())
                .uniqueNo(beanInterceptBO.getUniqueNo())
                .build();
    }

    public static InvokeDetailDO toInvokeDetailDO(BeanInterceptBO beanInterceptBO){

        return InvokeDetailDO.builder()
                .args(beanInterceptBO.getArgs())
                .serializeType(beanInterceptBO.getSerializeType())
                .beanName(beanInterceptBO.getBeanName())
                .className(beanInterceptBO.getClassName())
                .invokeNo(beanInterceptBO.getInvokeNo())
                .method(beanInterceptBO.getMethod())
                .status(0)
                .returnValue(beanInterceptBO.getReturnValue())
                .build();
    }
}
