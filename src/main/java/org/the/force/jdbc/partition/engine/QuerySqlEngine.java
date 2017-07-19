package org.the.force.jdbc.partition.engine;

import org.the.force.jdbc.partition.driver.JdbcPartitionConnection;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.factory.QueryExecutorFactory;
import org.the.force.jdbc.partition.resource.statement.AbstractPreparedStatement;

/**
 * Created by xuji on 2017/6/4.
 */
public class QuerySqlEngine extends AbstractPreparedStatement {

    protected final JdbcPartitionConnection jdbcPartitionConnection;

    protected final QueryExecutor queryExecutor;

    public QuerySqlEngine(JdbcPartitionConnection jdbcPartitionConnection, QueryExecutor queryExecutor) {
        this.jdbcPartitionConnection = jdbcPartitionConnection;
        this.queryExecutor = queryExecutor;
    }


}
