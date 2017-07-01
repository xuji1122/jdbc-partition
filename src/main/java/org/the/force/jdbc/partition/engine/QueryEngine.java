package org.the.force.jdbc.partition.engine;

import org.the.force.jdbc.partition.driver.JdbcPartitionConnection;
import org.the.force.jdbc.partition.engine.plan.QueryPlan;
import org.the.force.jdbc.partition.resource.statement.AbstractPreparedStatement;

/**
 * Created by xuji on 2017/6/4.
 */
public class QueryEngine extends AbstractPreparedStatement {

    protected final JdbcPartitionConnection jdbcPartitionConnection;

    protected final QueryPlan queryCompiler;

    public QueryEngine(JdbcPartitionConnection jdbcPartitionConnection, QueryPlan queryCompiler) {
        this.jdbcPartitionConnection = jdbcPartitionConnection;
        this.queryCompiler = queryCompiler;
    }


}
