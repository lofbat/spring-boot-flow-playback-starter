package io.github.lofbat.flow.dao;

import io.github.lofbat.flow.model.dataobject.InvokeInfoDO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by geqi on 2019/5/30.
 */
@Repository
public interface InvokeInfoDAO {

    Integer insert(InvokeInfoDO invokeInfoDO);

    Integer updateById(InvokeInfoDO invokeInfoDO);

    List<InvokeInfoDO> findByUniqueNo(Long uniqueId);
}
