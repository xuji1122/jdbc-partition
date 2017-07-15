package org.the.force.jdbc.partition.engine.result;

import org.the.force.jdbc.partition.engine.eval.TypedSqlValue;
import org.the.force.jdbc.partition.exception.SqlParseException;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by xuji on 2017/6/6.
 */
public class DataItemRow {

    private final Object[] cellValues;

    private int[] sqlTypes;

    private final Map<String, Integer> labelIndexMap;

    public static final DataItemRow EMPTY_DATA_ROW = new DataItemRow(null, null, null);

    public DataItemRow(Object[] cellValues, int[] sqlTypes, Map<String, Integer> labelIndexMap) {
        this.cellValues = cellValues;
        this.sqlTypes = sqlTypes;
        this.labelIndexMap = labelIndexMap;
    }

    public Object getValue(int columnIndex) {
        return new TypedSqlValue(cellValues[columnIndex], sqlTypes[columnIndex]);
    }

    public Object getValue(String key) throws SQLException {
        Integer index = labelIndexMap.get(key.toLowerCase());
        if (index == null) {
            throw new SqlParseException("key" + key + " 不存在");
        }
        return new TypedSqlValue(cellValues[index], sqlTypes[index]);
    }

}
