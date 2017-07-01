package org.the.force.jdbc.partition.engine.executor.query.item;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/7.
 * 查询结果结构的定义
 * 与 db物理查询结果  一对一或一对多  但是QueryField结构是一致的
 */
public class RowQueryItems implements ResultSetMetaData {

    private final Item[] items;

    public RowQueryItems(FunctionalItem[] items) {
        this.items = items;
    }

    public int getColumnCount() throws SQLException {
        return items.length;
    }

    public String getColumnLabel(int column) throws SQLException {
        return items[column - 1].getColumnLabel();
    }

    public String getColumnName(int column) throws SQLException {
        return items[column - 1].getColumnName();
    }

    public String getSchemaName(int column) throws SQLException {
        return items[column - 1].getSchemaName();
    }

    public String getTableName(int column) throws SQLException {
        return items[column - 1].getTableName();
    }

    public String getCatalogName(int column) throws SQLException {
        return items[column - 1].getCatalogName();
    }

    public int getColumnType(int column) throws SQLException {
        return 0;
    }

    public String getColumnTypeName(int column) throws SQLException {
        return null;
    }

    public int getPrecision(int column) throws SQLException {
        return 0;
    }

    public int getScale(int column) throws SQLException {
        return 0;
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        return false;
    }

    public boolean isSearchable(int column) throws SQLException {
        return false;
    }

    public boolean isCurrency(int column) throws SQLException {
        return false;
    }

    public int isNullable(int column) throws SQLException {
        return 0;
    }

    public boolean isSigned(int column) throws SQLException {
        return false;
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        return 0;
    }



    public boolean isReadOnly(int column) throws SQLException {
        return false;
    }

    public boolean isWritable(int column) throws SQLException {
        return false;
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    public String getColumnClassName(int column) throws SQLException {
        return null;
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
