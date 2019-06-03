package io.github.lofbat.flow.model.dataobject;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * Created by geqi on 2019/5/30.
 */
@Data
@Builder
@ToString(callSuper = true)
public class InvokeDetailDO extends DataObject {

    private String invokeNo;

    private String className;

    private String beanName;

    private String method;

    private String args;

    private Integer serializeType;

    private String returnValue;

    private Integer status;
}
