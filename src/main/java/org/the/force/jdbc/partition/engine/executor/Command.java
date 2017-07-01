package org.the.force.jdbc.partition.engine.executor;

import org.the.force.jdbc.partition.resource.connection.ConnectionAdapter;
import org.the.force.jdbc.partition.engine.parameter.SqlParameter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by xuji on 2017/6/2.
 * 一个ExecutorCommand实例代表statementEngine发起的一次逻辑sql的执行
 */
public class Command {

    private final ConnectionAdapter connectionAdapter;

    private final ThreadPoolExecutor threadPool;

    private final ExecutorConfig executorConfig;

    public Command(ConnectionAdapter connectionAdapter, ThreadPoolExecutor threadPool, ExecutorConfig executorConfig) {
        this.connectionAdapter = connectionAdapter;
        this.threadPool = threadPool;
        this.executorConfig = executorConfig;
    }

    public void initConnection(String physicDbName) throws SQLException {
        connectionAdapter.initConnection(physicDbName);
    }

    public Connection getConnection(String physicDbName) throws SQLException {
        return connectionAdapter.getConnection(physicDbName);
    }

    public ExecutorService getExecutorService() {
        return threadPool;
    }

    public void configStatement(Statement statement) throws SQLException {

    }

    public void setParams(PreparedStatement preparedStatement, List<SqlParameter> sqlParameters) throws SQLException {
        for (int i = 0, limit = sqlParameters.size(); i < limit; i++) {
            SqlParameter sqlParameter = sqlParameters.get(i);
            sqlParameter.set(i + 1, preparedStatement);
        }
        sqlParameters.clear();
    }

    public ExecutorConfig getExecutorConfig() {
        return executorConfig;
    }


}

