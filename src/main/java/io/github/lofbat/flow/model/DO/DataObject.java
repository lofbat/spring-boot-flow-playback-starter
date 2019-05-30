package io.github.lofbat.flow.model.DO;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by geqi on 2019/5/30.
 */
public class DataObject implements Serializable {

    /**
     * 自增主键
     */
    private Long id;

    private Date createdAt;

    private Date updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataObject that = (DataObject) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
