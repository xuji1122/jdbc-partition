package org.the.force.jdbc.partition.engine;

import org.the.force.jdbc.partition.driver.JdbcPartitionConnection;
import org.the.force.jdbc.partition.engine.executor.factory.QueryExecutionFactory;
import org.the.force.jdbc.partition.resource.statement.AbstractPreparedStatement;

/**
 * Created by xuji on 2017/6/4.
 */
public class QuerySqlEngine extends AbstractPreparedStatement {

    protected final JdbcPartitionConnection jdbcPartitionConnection;

    protected final QueryExecutionFactory queryCompiler;

    public QuerySqlEngine(JdbcPartitionConnection jdbcPartitionConnection, QueryExecutionFactory queryCompiler) {
        this.jdbcPartitionConnection = jdbcPartitionConnection;
        this.queryCompiler = queryCompiler;
    }


}
