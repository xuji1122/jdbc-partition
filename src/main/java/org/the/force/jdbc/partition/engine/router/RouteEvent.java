package org.the.force.jdbc.partition.engine.router;

import org.the.force.jdbc.partition.engine.executor.SqlExecutionContext;
import org.the.force.jdbc.partition.engine.value.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.PartitionEvent;

/**
 * Created by xuji on 2017/7/10.
 */
public class RouteEvent {

    private final LogicTableConfig logicTableConfig;

    private final PartitionEvent.EventType eventType;

    private final LogicSqlParameterHolder logicSqlParameterHolder;

    private SqlExecutionContext sqlExecutionContext;

    public RouteEvent(LogicTableConfig logicTableConfig, PartitionEvent.EventType eventType, LogicSqlParameterHolder logicSqlParameterHolder) {
        this.logicTableConfig = logicTableConfig;
        this.eventType = eventType;
        this.logicSqlParameterHolder = logicSqlParameterHolder;
    }

    public LogicSqlParameterHolder getLogicSqlParameterHolder() {
        return logicSqlParameterHolder;
    }

    public LogicTableConfig getLogicTableConfig() {
        return logicTableConfig;
    }

    public PartitionEvent.EventType getEventType() {
        return eventType;
    }

    public boolean isInsertInto() {
        return this.eventType == PartitionEvent.EventType.INSERT;
    }

    public SqlExecutionContext getSqlExecutionContext() {
        return sqlExecutionContext;
    }

    public void setSqlExecutionContext(SqlExecutionContext sqlExecutionContext) {
        this.sqlExecutionContext = sqlExecutionContext;
    }
}
