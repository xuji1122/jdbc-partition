package org.the.force.jdbc.partition.resource.resultset;

import org.the.force.jdbc.partition.resource.table.model.LogicTable;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/6.
 */
public class RSMetaDataAdapter extends WrappedRSMetaData {

    private final LogicTable logicTable;

    private Integer columnCount;

    public RSMetaDataAdapter(ResultSetMetaData original, LogicTable logicTable) {
        super(original);
        this.logicTable = logicTable;
    }

    public int getColumnCount() throws SQLException {
        int originalCount = getOriginal().getColumnCount();
        if (columnCount != null && columnCount > 0 && originalCount > columnCount) {
            return columnCount;
        }
        return originalCount;
    }

    public String getSchemaName(int column) throws SQLException {
        return logicTable.getSchema();
    }

    public String getTableName(int column) throws SQLException {
        return logicTable.getTableName();
    }

    public String getCatalogName(int column) throws SQLException {
        return logicTable.getCatalog();
    }

    public void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
    }


}
