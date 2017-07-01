package org.the.force.jdbc.partition.engine.executor.query.item;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/8.
 */
public class DbDirectItem implements Item {

    private final int index;
    private final ResultSetMetaData rsd;

    public DbDirectItem(int index, ResultSetMetaData rsd) {
        this.index = index;
        this.rsd = rsd;
    }

    public String getColumnName() throws SQLException {
        return rsd.getColumnName(index + 1);
    }

    public String getSchemaName() throws SQLException {
        return rsd.getSchemaName(index + 1);
    }

    public String getTableName() throws SQLException {
        return rsd.getTableName(index + 1);
    }

    public String getCatalogName() throws SQLException {
        return rsd.getCatalogName(index + 1);
    }

    public String getColumnLabel() throws SQLException {
        return rsd.getColumnLabel(index + 1);
    }


}
