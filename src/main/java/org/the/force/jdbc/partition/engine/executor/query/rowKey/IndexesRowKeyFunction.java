package org.the.force.jdbc.partition.engine.executor.query.rowKey;

import org.the.force.jdbc.partition.engine.executor.query.item.RowQueryItems;
import org.the.force.jdbc.partition.engine.result.DataItemRow;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/7.
 */
public class IndexesRowKeyFunction implements RowKeyFunction {

    private final boolean allItemAsKey;
    private final int[] keyIndexes;

    public IndexesRowKeyFunction(boolean allItemAsKey, int[] keyIndexes) {
        this.allItemAsKey = allItemAsKey;
        if (allItemAsKey) {
            this.keyIndexes = null;
        } else {
            this.keyIndexes = keyIndexes;
        }
    }

    public Object getRowKey(RowQueryItems rowQueryFiled, DataItemRow dataRow) throws SQLException {

        if (allItemAsKey) {
            int columnCount = rowQueryFiled.getColumnCount();
            //TODO 小于0的情况
            if (columnCount == 1) {
                Object value = dataRow.getValue(0);
                if (value == null) {
                    return "NULL";
                } else {
                    return value.toString();
                }
            } else {
                String[] values = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    Object value = dataRow.getValue(i);
                    if (value == null) {
                        values[i] = "NULL";
                    } else {
                        values[i] = value.toString();
                    }
                }
                return new StringsRowKey(values);
            }
        } else {
            if (keyIndexes.length == 1) {
                Object value = dataRow.getValue(keyIndexes[0]);
                if (value == null) {
                    return "NULL";
                } else {
                    return value.toString();
                }
            } else {
                String[] values = new String[keyIndexes.length];
                for (int i = 0; i < keyIndexes.length; i++) {
                    Object value = dataRow.getValue(keyIndexes[i]);
                    if (value == null) {
                        values[i] = "NULL";
                    } else {
                        values[i] = value.toString();
                    }
                }
                return new StringsRowKey(values);
            }
        }
    }
}
