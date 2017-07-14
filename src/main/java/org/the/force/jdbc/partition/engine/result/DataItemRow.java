package org.the.force.jdbc.partition.engine.result;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/6.
 */
public class DataItemRow {

    private final Object[] cellValues;

    private final ResultSetMetaData resultSetMetaData;

    public static final DataItemRow EMPTY_DATA_ROW = new DataItemRow();

    private DataItemRow() {
        resultSetMetaData = null;
        cellValues = new Object[] {};
    }

    public DataItemRow(ResultSet rs, ResultSetMetaData resultSetMetaData) throws SQLException {
        this.resultSetMetaData = resultSetMetaData;
        int columnCount = resultSetMetaData.getColumnCount();
        cellValues = new Object[columnCount];
        for (int i = 0; i < columnCount; i++) {
            cellValues[i] = rs.getObject(i + 1);
        }
    }

    //从0开始
    public Object getValue(int columnIndex){
        return cellValues[columnIndex];
    }

//    public Object getValue(String tableName, String label) throws SQLException {
//        int columnCount = resultSetMetaData.getColumnCount();
//        for (int i = 0; i < columnCount; i++) {
//            String tableName2 = resultSetMetaData.getTableName(i + 1);
//            String label2 = resultSetMetaData.getColumnLabel(i + 1);
//            if (tableName.equalsIgnoreCase(tableName2) && label.equalsIgnoreCase(label2)) {
//                return cellValues[i];
//            }
//        }
//        return null;
//    }

    public ResultSetMetaData getResultSetMetaData() {
        return resultSetMetaData;
    }
}
