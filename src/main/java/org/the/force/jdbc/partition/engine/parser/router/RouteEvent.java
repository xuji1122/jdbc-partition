package org.the.force.jdbc.partition.engine.parser.router;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumn;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumnValue;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/10.
 */
public class RouteEvent {

    private final SQLStatement sqlStatement;

    private final LogicTableConfig logicTableConfig;

    private final PartitionEvent.EventType eventType;

    private final LogicSqlParameterHolder logicSqlParameterHolder;

    //只有在insert时使用
    private Map<Integer, SqlColumnValue> partitionColumnMap;
    //只有在insert时使用
    private List<SQLInsertStatement.ValuesClause> valuesClauseList;

    private Map<SqlColumn, SQLExpr> columnValueMap;

    private Map<SqlColumn, SQLInListExpr> sqlInValuesMap;


    public RouteEvent(SQLStatement sqlStatement, LogicTableConfig logicTableConfig, PartitionEvent.EventType eventType, LogicSqlParameterHolder logicSqlParameterHolder) {
        this.sqlStatement = sqlStatement;
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

    public SQLStatement getSqlStatement() {
        return sqlStatement;
    }

    public List<SQLInsertStatement.ValuesClause> getValuesClauseList() {
        return valuesClauseList;
    }

    public void setValuesClauseList(List<SQLInsertStatement.ValuesClause> valuesClauseList) {
        this.valuesClauseList = valuesClauseList;
    }

    public Map<SqlColumn, SQLExpr> getColumnValueMap() {
        return columnValueMap;
    }

    public void setColumnValueMap(Map<SqlColumn, SQLExpr> columnValueMap) {
        this.columnValueMap = columnValueMap;
    }

    public Map<SqlColumn, SQLInListExpr> getSqlInValuesMap() {
        return sqlInValuesMap;
    }

    public void setSqlInValuesMap(Map<SqlColumn, SQLInListExpr> sqlInValuesMap) {
        this.sqlInValuesMap = sqlInValuesMap;
    }

    public Map<Integer, SqlColumnValue> getPartitionColumnMap() {
        return partitionColumnMap;
    }

    public void setPartitionColumnMap(Map<Integer, SqlColumnValue> partitionColumnMap) {
        this.partitionColumnMap = partitionColumnMap;
    }

    public boolean isInsertInto() {
        return this.eventType == PartitionEvent.EventType.INSERT;
    }

}
