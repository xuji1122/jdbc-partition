package org.the.force.jdbc.partition.engine.executor.dml;

import org.the.force.jdbc.partition.engine.executor.physic.LinedParameters;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicTableExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PreparedPhysicSqlExecutor;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.table.TableConditionParser;
import org.the.force.jdbc.partition.engine.router.DefaultTableRouter;
import org.the.force.jdbc.partition.engine.router.MySqlPartitionSqlOutput;
import org.the.force.jdbc.partition.engine.router.RouteEvent;
import org.the.force.jdbc.partition.engine.router.TableRouter;
import org.the.force.jdbc.partition.engine.sql.SqlTablePartition;
import org.the.force.jdbc.partition.engine.sql.table.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.engine.value.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionEvent;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/6/1.
 * 更新和删除的解析类
 */
public class UpdateDelParser {

    private final UpdateDelParserAdapter updateDelParserAdapter;

    protected final LogicDbConfig logicDbConfig;

    protected ExprConditionalSqlTable exprSqlTable;

    private final PartitionEvent.EventType eventType;


    private final TableRouter tableRouter;

    public UpdateDelParser(LogicDbConfig logicDbConfig, UpdateDelParserAdapter updateDelParserAdapter) throws Exception {
        this.logicDbConfig = logicDbConfig;
        this.updateDelParserAdapter = updateDelParserAdapter;
        this.eventType = updateDelParserAdapter.getEventType();
        exprSqlTable = new ExprConditionalSqlTable(logicDbConfig, updateDelParserAdapter.getSQLExprTableSource());
        new TableConditionParser(logicDbConfig, exprSqlTable, updateDelParserAdapter.getCondition());
        SqlTableReferParser parser = new SqlTableReferParser(logicDbConfig, updateDelParserAdapter.getSQLStatement(), exprSqlTable);
        exprSqlTable.setAlias(parser.getTableAlias());

        tableRouter = new DefaultTableRouter(logicDbConfig, updateDelParserAdapter.getSQLStatement(), exprSqlTable);
    }


    public void addSqlLine(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {
        LogicTableConfig[] configPair = logicDbConfig.getLogicTableManager(exprSqlTable.getTableName()).getLogicTableConfig();
        LogicTableConfig logicTableConfig = configPair[0];
        //TODO 数据迁移时老区新区 周新区 update时策略 新区老区双写，表格主键的获取
        RouteEvent routeEvent = new RouteEvent(logicTableConfig, eventType, logicSqlParameterHolder);
        Map<Partition, SqlTablePartition> partitionSqlTablePartitionSqlMap = tableRouter.route(routeEvent);
        for (Map.Entry<Partition, SqlTablePartition> entry : partitionSqlTablePartitionSqlMap.entrySet()) {
            Partition partition = entry.getKey();
            StringBuilder sqlSb = new StringBuilder();
            MySqlPartitionSqlOutput mySqlPartitionSqlOutput = new MySqlPartitionSqlOutput(sqlSb, logicDbConfig, routeEvent, entry.getValue());
            updateDelParserAdapter.getSQLStatement().accept(mySqlPartitionSqlOutput);
            List<SqlParameter> newSqlParameters = mySqlPartitionSqlOutput.getSqlParameterList();
            String sql = sqlSb.toString();
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
