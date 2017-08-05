package org.the.force.jdbc.partition.engine.router;

import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.PartitionEvent;

/**
 * Created by xuji on 2017/7/10.
 */
public class RouteEvent {

    private final LogicTableConfig logicTableConfig;

    private final PartitionEvent.EventType eventType;

    private final SqlLineExecRequest sqlLineExecRequest;

    public RouteEvent(LogicTableConfig logicTableConfig, PartitionEvent.EventType eventType, SqlLineExecRequest sqlLineExecRequest) {
        this.logicTableConfig = logicTableConfig;
        this.eventType = eventType;
        this.sqlLineExecRequest = sqlLineExecRequest;
    }

    public LogicTableConfig getLogicTableConfig() {
        return logicTableConfig;
    }

    public PartitionEvent.EventType getEventType() {
        return eventType;
    }

    public SqlLineExecRequest getSqlLineExecRequest() {
        return sqlLineExecRequest;
    }
}
