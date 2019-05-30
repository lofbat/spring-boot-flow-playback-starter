package io.github.lofbat.flow.model.DO;

/**
 * Created by geqi on 2019/5/30.
 */
public class InvokeInfoDO extends DataObject{

    private String app;

    private Integer uniqueId;

    private Integer invokeId;

    private Integer serialNo;

    private Integer type;

    private Integer status;

    /**
     * 扩展字段（保留扩展性）
     */
    private String ext;
}
