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
public class InvokeInfoDO extends DataObject{

    private String app;

    private String uniqueNo;

    private String invokeNo;

    private Integer serialNo;

    private Integer type;

    private Integer status;

    /**
     * 扩展字段（保留扩展性）
     */
    private String ext;
}
