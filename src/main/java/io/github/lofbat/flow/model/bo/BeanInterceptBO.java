package io.github.lofbat.flow.model.bo;

import lombok.Builder;
import lombok.Data;

/**
 * Created by geqi on 2019/5/29.
 */
@Data
@Builder
public class BeanInterceptBO {

    private String app;

    private String uniqueNo;

    private String invokeNo;

    private String className;

    private String beanName;

    private String method;

    private String args;

    private Integer serializeType;

    private String returnValue;

    private Integer serialNo;

    private Integer type;

    /**
     * 扩展字段（保留扩展性）
     */
    private String ext;
}
