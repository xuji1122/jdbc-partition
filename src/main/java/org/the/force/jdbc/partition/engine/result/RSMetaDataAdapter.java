package org.the.force.jdbc.partition.engine.result;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/6.
 */
public class RSMetaDataAdapter extends WrappedRSMetaData {

    private final String logicTableName;

    private final String logicDbName;

    private Integer columnCount;

    public RSMetaDataAdapter(ResultSetMetaData original, String logicTableName, String logicDbName) {
        super(original);
        this.logicTableName = logicTableName;
        this.logicDbName = logicDbName;
    }

    public int getColumnCount() throws SQLException {
        int originalCount = getOriginal().getColumnCount();
        if (columnCount != null && columnCount > 0 && originalCount > columnCount) {
            return columnCount;
        }
        return originalCount;
    }

    public String getSchemaName(int column) throws SQLException {
        return logicDbName;
    }

    public String getTableName(int column) throws SQLException {
        return logicTableName;
    }

    public String getCatalogName(int column) throws SQLException {
        return logicTableName;
    }

    public void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
    }


}
