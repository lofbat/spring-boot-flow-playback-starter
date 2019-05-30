package io.github.lofbat.flow.dao;

import io.github.lofbat.flow.model.DO.InvokeDetailDO;
import org.springframework.stereotype.Repository;

/**
 * Created by geqi on 2019/5/30.
 */
@Repository
public interface InvokeDetailDAO {

    Integer insert(InvokeDetailDO invokeDetailDO);

    Integer update(InvokeDetailDO invokeDetailDO);

    InvokeDetailDO getByInvokeId(Long invokeId);
}
