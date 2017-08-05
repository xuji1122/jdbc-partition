package org.the.force.jdbc.partition.resource;

import org.the.force.jdbc.partition.resource.connection.ConnectionAdapter;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.executor.SqlExecutorManager;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by xuji on 2017/6/2.
 * 一个ExecutorCommand实例代表statementEngine发起的一次逻辑sql的执行
 */
public class SqlExecResource {

    private final ConnectionAdapter connectionAdapter;

    private final ThreadPoolExecutor threadPool;

    private final SqlExecutorManager sqlExecutorManager;

    private final LogicDbConfig logicDbConfig;

    public SqlExecResource(ConnectionAdapter connectionAdapter, ThreadPoolExecutor threadPool, SqlExecutorManager sqlExecutorManager, LogicDbConfig logicDbConfig) {
        this.connectionAdapter = connectionAdapter;
        this.threadPool = threadPool;
        this.sqlExecutorManager = sqlExecutorManager;
        this.logicDbConfig = logicDbConfig;
    }

    public ConnectionAdapter getConnectionAdapter() {
        return connectionAdapter;
    }

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public SqlExecutorManager getSqlExecutorManager() {
        return sqlExecutorManager;
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }
}

