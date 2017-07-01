package org.the.force.jdbc.partition.engine.result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/6.
 */
public class DataItemRow {

    private final Object[] cellValues;

    public static final DataItemRow EMPTY_DATA_ROW = new DataItemRow();



    private DataItemRow() {
        cellValues = new Object[] {};
    }

    public DataItemRow(ResultSet rs, ResultSetMetaData resultSetMetaData) throws SQLException {
        int columnCount = resultSetMetaData.getColumnCount();
        cellValues = new Object[columnCount];
        for (int i = 0; i < columnCount; i++) {
            cellValues[i] = rs.getObject(i + 1);
        }
    }

    //从0开始
    public Object getValue(int columnIndex) {
        return cellValues[columnIndex];
    }

}
