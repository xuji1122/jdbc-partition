package org.the.force.jdbc.partition.engine.router;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluatorFactory;
import org.the.force.jdbc.partition.engine.parameter.SqlParameter;
import org.the.force.jdbc.partition.engine.sqlelements.sqltable.InsertSqlTable;
import org.the.force.jdbc.partition.engine.sqlelements.SqlColumnValue;
import org.the.force.jdbc.partition.engine.sqlelements.SqlRefer;
import org.the.force.jdbc.partition.engine.sqlelements.SqlTablePartition;
import org.the.force.jdbc.partition.engine.sqlelements.SqlTablePartitionSql;
import org.the.force.jdbc.partition.engine.router.output.MySqlPartitionSqlOutput;
import org.the.force.jdbc.partition.exception.PartitionConfigException;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionColumnValue;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.jdbc.partition.rule.PartitionRule;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by xuji on 2017/7/10.
 * 逻辑表路由实现类
 */
public class InsertTableRouter implements TableRouter {

    protected final LogicDbConfig logicDbConfig;

    private final SQLInsertStatement sqlStatement;

    protected final InsertSqlTable exprSqlTable;


    public InsertTableRouter(LogicDbConfig logicDbConfig, SQLInsertStatement sqlStatement, InsertSqlTable exprSqlTable) {
        this.logicDbConfig = logicDbConfig;
        this.sqlStatement = sqlStatement;
        this.exprSqlTable = exprSqlTable;
    }

    public Map<Partition, SqlTablePartitionSql> route(RouteEvent routeEvent) throws SQLException {
        //insert into 单独处理
        List<SQLInsertStatement.ValuesClause> valuesClauseList = sqlStatement.getValuesList();
        SqlExprEvaluatorFactory sqlExprEvaluatorFactory = logicDbConfig.getSqlExprEvaluatorFactory();
        LogicTableConfig logicTableConfig = routeEvent.getLogicTableConfig();
        InsertSqlTable insertSqlTable = exprSqlTable;
        PartitionEvent partitionEvent = new PartitionEvent(logicTableConfig.getLogicTableName(), routeEvent.getEventType(), logicTableConfig.getPartitionSortType(),
            logicTableConfig.getPartitionColumnConfigs());
        partitionEvent.setPartitions(logicTableConfig.getPartitions());
        partitionEvent.setPhysicDbs(logicTableConfig.getPhysicDbs());
        SortedSet<String> sortedSet = logicTableConfig.getPartitionColumnNames();
        Map<Partition, SqlTablePartition> subsMap = new ConcurrentSkipListMap<>(routeEvent.getLogicTableConfig().getPartitionSortType().getComparator());

        int count = 0;
        SqlExprEvalContext sqlExprEvalContext = new SqlExprEvalContext(routeEvent.getLogicSqlParameterHolder());
        for (SQLInsertStatement.ValuesClause valuesClause : valuesClauseList) {
            List<SQLExpr> sqlExprList = valuesClause.getValues();
            int size = sqlExprList.size();
            TreeSet<PartitionColumnValue> partitionColumnValueTreeSet = new TreeSet<>();
            for (int i = 0; i < size; i++) {
                count++;
                SqlRefer sqlRefer = insertSqlTable.getColumnMap().get(i);
                if (!sortedSet.contains(sqlRefer.getName().toLowerCase())) {
                    continue;
                }
                if (i == size - 1) {
                    throw new PartitionConfigException("insert必须指定partition column");
                }
                SqlExprEvaluator sqlExprEvaluator = insertSqlTable.getEvaluatorMap().get(count);
                if (sqlExprEvaluator == null) {
                    sqlExprEvaluator = sqlExprEvaluatorFactory.matchSqlExprEvaluator(sqlExprList.get(i));
                }

                Object value = sqlExprEvaluator.eval(sqlExprEvalContext, null);
                if (value == null) {
                    throw new SqlParseException("partition value == null");
                }
                partitionColumnValueTreeSet.add(new SqlColumnValue(sqlRefer.getName(), value));
            }
            PartitionRule partitionRule = logicTableConfig.getPartitionRule();

            SortedSet<Partition> partitions = partitionRule.selectPartitions(partitionEvent, partitionColumnValueTreeSet);
            Partition partition = partitions.iterator().next();
            if (!subsMap.containsKey(partition)) {
                subsMap.put(partition, new SqlTablePartition(exprSqlTable, partition));
            }
            SqlTablePartition sqlTablePartition = subsMap.get(partition);
            sqlTablePartition.getValuesClauses().add(valuesClause);
        }
        Map<Partition, SqlTablePartitionSql> sqlTablePartitions = new ConcurrentSkipListMap<>(routeEvent.getLogicTableConfig().getPartitionSortType().getComparator());
        for (Map.Entry<Partition, SqlTablePartition> entry : subsMap.entrySet()) {
            StringBuilder sqlSb = new StringBuilder();
            MySqlPartitionSqlOutput mySqlPartitionSqlOutput = new MySqlPartitionSqlOutput(sqlSb, logicDbConfig, routeEvent, entry.getValue());
            sqlStatement.accept(mySqlPartitionSqlOutput);
            List<SqlParameter> list = mySqlPartitionSqlOutput.getSqlParameterList();
            String sql = sqlSb.toString();
            sqlTablePartitions.put(entry.getKey(), new SqlTablePartitionSql(sql, list));
        }
        return sqlTablePartitions;

    }

}
