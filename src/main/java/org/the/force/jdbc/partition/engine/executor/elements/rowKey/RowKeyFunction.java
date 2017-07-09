package org.the.force.jdbc.partition.engine.executor.elements.rowKey;

import org.the.force.jdbc.partition.engine.executor.elements.item.RowQueryItems;
import org.the.force.jdbc.partition.engine.result.DataItemRow;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/7.
 */
public interface RowKeyFunction {

    /**
     * String 或者 StringsRowKey
     *
     * @param rowQueryFiled
     * @param dataRow
     * @return
     */
    Object getRowKey(RowQueryItems rowQueryFiled, DataItemRow dataRow) throws SQLException;

}
