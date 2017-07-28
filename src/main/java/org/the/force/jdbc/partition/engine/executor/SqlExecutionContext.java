package org.the.force.jdbc.partition.engine.executor;

import org.the.force.jdbc.partition.engine.value.LogicSqlParameterHolder;

/**
 * Created by xuji on 2017/5/18.
 */
public class SqlExecutionContext {

    private final LogicSqlParameterHolder logicSqlParameterHolder;

    private SqlExecutionResource sqlExecutionResource;

    public SqlExecutionContext(LogicSqlParameterHolder logicSqlParameterHolder) {
        this.logicSqlParameterHolder = logicSqlParameterHolder;
    }

    public LogicSqlParameterHolder getLogicSqlParameterHolder() {
        return logicSqlParameterHolder;
    }

    public SqlExecutionResource getSqlExecutionResource() {
        return sqlExecutionResource;
    }

    public void setSqlExecutionResource(SqlExecutionResource sqlExecutionResource) {
        this.sqlExecutionResource = sqlExecutionResource;
    }
}
