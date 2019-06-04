package io.github.lofbat.flow.dao;

import io.github.lofbat.flow.model.dataobject.InvokeDetailDO;
import org.springframework.stereotype.Repository;

/**
 * Created by geqi on 2019/5/30.
 */
@Repository
public interface InvokeDetailDAO {

    Integer insert(InvokeDetailDO invokeDetailDO);

    Integer updateById(InvokeDetailDO invokeDetailDO);

    InvokeDetailDO getByInvokeNo(Long invokeId);
}
