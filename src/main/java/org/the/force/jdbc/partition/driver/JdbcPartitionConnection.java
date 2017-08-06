package org.the.force.jdbc.partition.driver;

import org.the.force.jdbc.partition.driver.statement.PPreparedStatement;
import org.the.force.jdbc.partition.driver.statement.PStatement;
import org.the.force.jdbc.partition.engine.stmt.LogicStmt;
import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.engine.stmt.impl.MultiSqlFactory;
import org.the.force.jdbc.partition.exception.UnsupportedSqlOperatorException;
import org.the.force.jdbc.partition.resource.SqlExecResource;
import org.the.force.jdbc.partition.resource.connection.AbstractConnection;
import org.the.force.jdbc.partition.resource.connection.ConnectionAdapter;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.db.mysql.MySqlDdMetaDataImpl;
import org.the.force.jdbc.partition.resource.executor.SqlExecutorManager;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by xuji on 2017/5/15.
 */
public class JdbcPartitionConnection extends AbstractConnection {

    private final LogicDbConfig logicDbConfig;

    private final SqlExecutorManager sqlExecutorManager;

    private final ThreadPoolExecutor threadPoolExecutor;

    private final ConnectionAdapter connectionAdapter;

    private final List<Statement> openedStatements = Collections.synchronizedList(new ArrayList<Statement>(2));

    public JdbcPartitionConnection(LogicDbConfig logicDbConfig, SqlExecutorManager sqlExecutorManager, ThreadPoolExecutor threadPoolExecutor) {
        this.logicDbConfig = logicDbConfig;
        this.sqlExecutorManager = sqlExecutorManager;
        this.threadPoolExecutor = threadPoolExecutor;
        this.connectionAdapter = new ConnectionAdapter(logicDbConfig);
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public SqlExecutorManager getSqlExecutorManager() {
        return sqlExecutorManager;
    }

    public ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }

    public ConnectionAdapter getConnectionAdapter() {
        return connectionAdapter;
    }


    /**
     * sql执行总入口
     *
     * @param logicStmt
     * @param logicStmtConfig
     * @return
     * @throws SQLException
     */
    public PResult executeLogicSql(LogicStmt logicStmt, LogicStmtConfig logicStmtConfig) throws SQLException {
        SqlExecResource sqlExecResource = new SqlExecResource(getConnectionAdapter(), getThreadPoolExecutor(), getSqlExecutorManager(), getLogicDbConfig());
        return logicStmt.execute(sqlExecResource, logicStmtConfig);
    }

    public PStatement createStatement() throws SQLException {
        PStatement statement = new PStatement(this);
        openedStatements.add(statement);
        return statement;
    }

    public PPreparedStatement prepareStatement(String sql) throws SQLException {
        PPreparedStatement pstmt = new PPreparedStatement(this, MultiSqlFactory.getLogicSql(sql));
        openedStatements.add(pstmt);
        return pstmt;
    }


    public void close() throws SQLException {
        connectionAdapter.closeConnection();
    }

    public boolean isClosed() throws SQLException {
        return connectionAdapter.isClosed();
    }

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        connectionAdapter.setAutoCommit(autoCommit);
    }

    public boolean getAutoCommit() throws SQLException {
        return connectionAdapter.getAutoCommit();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        connectionAdapter.setReadOnly(readOnly);
    }

    public boolean isReadOnly() throws SQLException {
        return connectionAdapter.isReadOnly();
    }

    public void commit() throws SQLException {
        connectionAdapter.commit();
    }

    public void rollback() throws SQLException {
        connectionAdapter.rollback();
    }


    public void setTransactionIsolation(int level) throws SQLException {
        connectionAdapter.setTransactionIsolation(level);
    }

    public int getTransactionIsolation() throws SQLException {
        return connectionAdapter.getTransactionIsolation();
    }


    public String getSchema() throws SQLException {
        return logicDbConfig.getLogicDbName();
    }


    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isAssignableFrom(this.getClass());
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        try {
            return (T) this;
        } catch (Exception e) {
            //TODO
            throw new RuntimeException(e);
        }
    }

    public void setHoldability(int holdability) throws SQLException {
        throw new UnsupportedOperationException("setHoldability");
    }

    public int getHoldability() throws SQLException {
        return ResultSet.CLOSE_CURSORS_AT_COMMIT;
    }

    public boolean isValid(int timeout) throws SQLException {
        return this.isClosed();
    }

    public boolean removeStatement(PStatement arg0) {

        return openedStatements.remove(arg0);

    }

    public PStatement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        PStatement stmt = createStatement();
        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);
        return stmt;
    }

    public PStatement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        PStatement stmt = createStatement(resultSetType, resultSetConcurrency);
        stmt.setResultSetHoldability(resultSetHoldability);
        return stmt;
    }

    public PPreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        PPreparedStatement stmt = new PPreparedStatement(this, new LogicStmtConfig(autoGeneratedKeys), MultiSqlFactory.getLogicSql(sql));
        openedStatements.add(stmt);
        return stmt;
    }

    public PPreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        PPreparedStatement stmt = prepareStatement(sql);
        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);
        stmt.setResultSetHoldability(resultSetHoldability);
        return stmt;
    }

    public PPreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        PPreparedStatement stmt = new PPreparedStatement(this, new LogicStmtConfig(columnIndexes), MultiSqlFactory.getLogicSql(sql));
        openedStatements.add(stmt);
        return stmt;
    }

    public PPreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        PPreparedStatement stmt = new PPreparedStatement(this, new LogicStmtConfig(columnNames), MultiSqlFactory.getLogicSql(sql));
        openedStatements.add(stmt);
        return stmt;
    }

    public PPreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        PPreparedStatement stmt = prepareStatement(sql);
        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);
        return stmt;
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return new MySqlDdMetaDataImpl(getLogicDbConfig(), getConnectionAdapter());
    }
}
