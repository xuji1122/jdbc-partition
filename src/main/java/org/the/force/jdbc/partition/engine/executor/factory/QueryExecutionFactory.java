package org.the.force.jdbc.partition.engine.executor.factory;

import org.the.force.jdbc.partition.engine.executor.QueryExecution;

/**
 * Created by xuji on 2017/6/4.
 */
public interface QueryExecutionFactory {

    QueryExecution getQueryExecution();

}
