package org.the.force.jdbc.partition.engine.executor.factory;

import org.the.force.jdbc.partition.engine.executor.QueryExecutor;

/**
 * Created by xuji on 2017/6/4.
 */
public interface QueryExecutorFactory {

    QueryExecutor getQueryExecutor();

}
