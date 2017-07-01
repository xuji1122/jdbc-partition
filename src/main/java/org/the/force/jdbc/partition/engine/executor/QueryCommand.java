package org.the.force.jdbc.partition.engine.executor;

import org.the.force.jdbc.partition.resource.connection.ConnectionAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by xuji on 2017/6/4.
 */
public abstract class QueryCommand extends Command {

    public QueryCommand(ConnectionAdapter connectionAdapter, ThreadPoolExecutor threadPool, ExecutorConfig executorConfig) {
        super(connectionAdapter, threadPool, executorConfig);
    }

    public abstract ResultSet executeQuery(Statement statement, String sql) throws SQLException;

}
