package io.github.lofbat.flow.model.DO;

import lombok.Data;
import lombok.ToString;

/**
 * Created by geqi on 2019/5/30.
 */
@Data
@ToString(callSuper = true)
public class InvokeDetailDO extends DataObject {

    private Integer invokeId;

    private String className;

    private String beanName;

    private String method;

    private String args;

    private String returnValue;

    private Integer status;
}
