package org.the.force.jdbc.partition.resource.resultset;

import org.the.force.jdbc.partition.exception.UnsupportedSqlOperatorException;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by xuji on 2017/7/2.
 */
public abstract class AbstractRSMetaData implements ResultSetMetaData {

    public int getColumnCount() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public boolean isAutoIncrement(int column) throws SQLException {
        return false;
    }

    public boolean isCaseSensitive(int column) throws SQLException {
        return false;
    }

    public boolean isSearchable(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public boolean isCurrency(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public int isNullable(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public boolean isSigned(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public int getColumnDisplaySize(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public String getColumnLabel(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public String getColumnName(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public String getCatalogName(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public String getSchemaName(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public String getTableName(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public int getPrecision(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public int getScale(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public int getColumnType(int column) throws SQLException {
        return Types.VARBINARY;
    }

    public String getColumnTypeName(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public boolean isReadOnly(int column) throws SQLException {
        return true;
    }

    public boolean isWritable(int column) throws SQLException {
        return false;
    }

    public boolean isDefinitelyWritable(int column) throws SQLException {
        return false;
    }

    public String getColumnClassName(int column) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }
}
