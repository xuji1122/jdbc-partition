package org.the.force.jdbc.partition.resource.resultset;

import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.rule.comparator.NameComparator;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/26.
 */
public class TableRSMetaDataAdapter extends WrappedRSMetaData {

    private final LogicDbConfig logicDbConfig;

    private Integer columnCount;


    public TableRSMetaDataAdapter(LogicDbConfig logicDbConfig, ResultSetMetaData original) {
        super(original);
        this.logicDbConfig = logicDbConfig;
    }

    public int getColumnCount() throws SQLException {
        int originalCount = getOriginal().getColumnCount();
        if (columnCount != null && columnCount > 0 && originalCount > columnCount) {
            return columnCount;
        }
        return originalCount;
    }

    public void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
    }

    public String getSchemaName(int column) throws SQLException {
        return logicDbConfig.getLogicDbName();
    }

    public String getTableName(int column) throws SQLException {
        String name = getOriginal().getTableName(column);
        return NameComparator.trimSuffix(name);
    }

    public String getCatalogName(int column) throws SQLException {
        return logicDbConfig.getLogicDbName();
    }


}
