package org.the.force.jdbc.partition.engine.executor.cursor;

import org.the.force.jdbc.partition.engine.executor.result.DataItemRow;
import org.the.force.jdbc.partition.engine.executor.result.RowCursor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by xuji on 2017/6/6.
 */
public class SingleRsCursor implements RowCursor {

    private final ResultSet rs;
    private final int[] sqlTypes;
    private final Map<String, Integer> labelIndexMap;

    public SingleRsCursor(ResultSet rs, final int[] sqlTypes, Map<String, Integer> labelIndexMap) {
        this.rs = rs;
        this.sqlTypes = sqlTypes;
        this.labelIndexMap = labelIndexMap;
    }

    public DataItemRow next() throws SQLException {
        if (rs.next()) {
            int size = sqlTypes.length;
            Object[] cellValues = new Object[size];
            for (int i = 1; i <= size; i++) {
                cellValues[i] = rs.getObject(i);
            }
            return new DataItemRow(cellValues, sqlTypes, labelIndexMap);
        } else {
            return null;
        }
    }

    public Map<String, Integer> getResultSetMetaMap() {
        return labelIndexMap;
    }

    public int[] getSqlTypes() {
        return sqlTypes;
    }

    public void close() throws SQLException {
        rs.close();
    }
}
