package org.the.force.jdbc.partition.resource.resultset;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/6.
 */
public class WrappedRSMetaData implements ResultSetMetaData {

    private final ResultSetMetaData original;

    public WrappedRSMetaData(ResultSetMetaData original) {
        this.original = original;
    }

    public ResultSetMetaData getOriginal() {
        return original;
    }

    public int getColumnCount() throws SQLException {
        return getOriginal().getColumnCount();
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        return getOriginal().isAutoIncrement(column);
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        return getOriginal().isCaseSensitive(column);
    }

    public boolean isSearchable(int column) throws SQLException {
        return getOriginal().isSearchable(column);
    }

    public boolean isCurrency(int column) throws SQLException {
        return getOriginal().isCaseSensitive(column);
    }

    public int isNullable(int column) throws SQLException {
        return getOriginal().isNullable(column);
    }

    public boolean isSigned(int column) throws SQLException {
        return getOriginal().isSigned(column);
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        return getOriginal().getColumnDisplaySize(column);
    }

    public String getColumnLabel(int column) throws SQLException {
        return getOriginal().getColumnLabel(column);
    }

    public String getColumnName(int column) throws SQLException {
        return getOriginal().getColumnName(column);
    }

    public String getSchemaName(int column) throws SQLException {
        return getOriginal().getSchemaName(column);
    }

    public int getPrecision(int column) throws SQLException {
        return getOriginal().getPrecision(column);
    }

    public int getScale(int column) throws SQLException {
        return getOriginal().getScale(column);
    }

    public String getTableName(int column) throws SQLException {
        return getOriginal().getTableName(column);
    }

    public String getCatalogName(int column) throws SQLException {
        return getOriginal().getCatalogName(column);
    }

    public int getColumnType(int column) throws SQLException {
        return getOriginal().getColumnType(column);
    }

    public String getColumnTypeName(int column) throws SQLException {
        return getOriginal().getColumnTypeName(column);
    }

    public boolean isReadOnly(int column) throws SQLException {
        return getOriginal().isReadOnly(column);
    }

    public boolean isWritable(int column) throws SQLException {
        return getOriginal().isWritable(column);
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        return getOriginal().isDefinitelyWritable(column);
    }

    public String getColumnClassName(int column) throws SQLException {
        return getOriginal().getColumnClassName(column);
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return getOriginal().unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return getOriginal().isWrapperFor(iface);
    }
}
