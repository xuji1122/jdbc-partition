package org.the.force.jdbc.partition.engine.executor.update;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.physic.LinedParameters;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicTableExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PreparedPhysicSqlExecutor;
import org.the.force.jdbc.partition.engine.parameter.SqlParameter;
import org.the.force.jdbc.partition.engine.parser.elements.ExprSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumn;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTablePartitionSql;
import org.the.force.jdbc.partition.engine.parser.router.RouteEvent;
import org.the.force.jdbc.partition.engine.parser.router.DefaultTableRouter;
import org.the.force.jdbc.partition.engine.parser.router.TableRouter;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.table.TableConditionParser;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/6/1.
 * 更新和删除的解析类
 */
public class UpdateDelParser {

    private final UpdateDelParserAdapter updateDelParserAdapter;

    protected final LogicDbConfig logicDbConfig;

    protected ExprSqlTable exprSqlTable;

    private final PartitionEvent.EventType eventType;

    private final SQLExpr where;

    private final Map<SqlColumn, SQLExpr> columnValueMap;//静态不变

    private final Map<SqlColumn, SQLInListExpr> sqlInValuesMap;//原始的in表达式，不可变


    private final TableRouter tableRouter;

    public UpdateDelParser(LogicDbConfig logicDbConfig, UpdateDelParserAdapter updateDelParserAdapter) throws Exception {
        this.logicDbConfig = logicDbConfig;
        this.updateDelParserAdapter = updateDelParserAdapter;
        this.eventType = updateDelParserAdapter.getEventType();
        exprSqlTable = SqlTableParser.getSQLExprTable(updateDelParserAdapter.getSQLExprTableSource(), logicDbConfig);
        TableConditionParser tableConditionParser = new TableConditionParser(logicDbConfig, exprSqlTable, updateDelParserAdapter.getCondition());
        this.where = tableConditionParser.getSubQueryResetWhere();
        columnValueMap = tableConditionParser.getCurrentTableColumnValueMap();
        sqlInValuesMap = tableConditionParser.getCurrentTableColumnInValuesMap();
        SqlTableReferParser parser = new SqlTableReferParser(logicDbConfig, updateDelParserAdapter.getSQLExprTableSource());
        exprSqlTable.setAlias(parser.getTableAlias());
        tableRouter = new DefaultTableRouter(logicDbConfig, exprSqlTable);
    }


    public void addSqlLine(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {
        LogicTableConfig[] configPair = logicDbConfig.getLogicTableManager(exprSqlTable.getTableName()).getLogicTableConfig();
        LogicTableConfig logicTableConfig = configPair[0];
        //TODO 数据迁移时老区新区 周新区 update时策略 新区老区双写，表格主键的获取
        RouteEvent routeEvent = new RouteEvent(updateDelParserAdapter.getSQLStatement(), logicTableConfig, eventType, logicSqlParameterHolder);
        routeEvent.setColumnValueMap(columnValueMap);
        routeEvent.setSqlInValuesMap(sqlInValuesMap);
        Map<Partition, SqlTablePartitionSql> partitionSqlTablePartitionSqlMap = tableRouter.route(routeEvent);
        for (Map.Entry<Partition, SqlTablePartitionSql> entry : partitionSqlTablePartitionSqlMap.entrySet()) {
            Partition partition = entry.getKey();
            List<SqlParameter> newSqlParameters = entry.getValue().getSqlParameters();
            String sql = entry.getValue().getSql();
            PhysicTableExecutor sqlExecutorRouter = physicDbExecutor.get(partition.getPhysicDbName());
            PreparedPhysicSqlExecutor preparedDbExecuteSql = sqlExecutorRouter.get(sql);
            if (preparedDbExecuteSql == null) {
                preparedDbExecuteSql = new PreparedPhysicSqlExecutor(sql, partition.getPhysicDbName());
                sqlExecutorRouter.add(preparedDbExecuteSql);
            }
            preparedDbExecuteSql.addParameters(new LinedParameters(logicSqlParameterHolder.getLineNumber(), newSqlParameters));
        }
    }
}
