package org.the.force.jdbc.partition.resource.db.mysql;

import org.the.force.jdbc.partition.resource.resultset.AbstractRSMetaData;

import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by xuji on 2017/7/2.
 */
public class MySqlDbMetaRsMeta extends AbstractRSMetaData {

    private final String[] names;

    private final int[] jdbcTypes;


    public MySqlDbMetaRsMeta(String[] names) {
        this(names, null);
    }


    public MySqlDbMetaRsMeta(String[] names, int[] jdbcTypes) {
        this.names = names;
        this.jdbcTypes = jdbcTypes;
    }

    public int getColumnCount() throws SQLException {
        return names.length;
    }

    public String getColumnLabel(int column) throws SQLException {
        return names[column - 1];
    }

    public String getColumnName(int column) throws SQLException {
        return names[column - 1];
    }

    public String getCatalogName(int column) throws SQLException {
        return "";
    }

    public String getSchemaName(int column) throws SQLException {
        return "";
    }

    public String getTableName(int column) throws SQLException {
        return null;
    }

    public int getColumnType(int column) throws SQLException {
        if (jdbcTypes == null || jdbcTypes.length == 0) {
            return Types.VARBINARY;
        }
        return jdbcTypes[column - 1];
    }

    public String getColumnTypeName(int column) throws SQLException {
        return "UNKNOWN";
    }


}
