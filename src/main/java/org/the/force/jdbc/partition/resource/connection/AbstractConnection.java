package org.the.force.jdbc.partition.resource.connection;

import org.the.force.jdbc.partition.exception.UnsupportedSqlOperatorException;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * Created by xuji on 2017/6/2.
 */
public class AbstractConnection implements Connection {

    public Statement createStatement() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public PreparedStatement prepareStatement(String sql) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public CallableStatement prepareCall(String sql) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String nativeSQL(String sql) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void setAutoCommit(boolean autoCommit) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean getAutoCommit() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void commit() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void rollback() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void close() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean isClosed() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public DatabaseMetaData getMetaData() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void setReadOnly(boolean readOnly) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean isReadOnly() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void setCatalog(String catalog) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getCatalog() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void setTransactionIsolation(int level) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public int getTransactionIsolation() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public SQLWarning getWarnings() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void clearWarnings() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void setHoldability(int holdability) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public int getHoldability() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public Savepoint setSavepoint() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public Savepoint setSavepoint(String name) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void rollback(Savepoint savepoint) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public Clob createClob() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public Blob createBlob() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public NClob createNClob() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public SQLXML createSQLXML() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean isValid(int timeout) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        throw new SQLClientInfoException("not support setClientInfo", null);
    }


    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        throw new SQLClientInfoException("not support setClientInfo", null);
    }


    public String getClientInfo(String name) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public Properties getClientInfo() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void setSchema(String schema) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public String getSchema() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void abort(Executor executor) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public int getNetworkTimeout() throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }


    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedSqlOperatorException();
    }
}
