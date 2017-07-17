package org.the.force.jdbc.partition.engine.router;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.jdbc.partition.engine.parameter.SqlParameter;
import org.the.force.jdbc.partition.engine.sqlelements.sqltable.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sqlelements.SqlColumnValue;
import org.the.force.jdbc.partition.engine.sqlelements.SqlRefer;
import org.the.force.jdbc.partition.engine.sqlelements.SqlTablePartition;
import org.the.force.jdbc.partition.engine.sqlelements.SqlTablePartitionSql;
import org.the.force.jdbc.partition.engine.router.output.MySqlPartitionSqlOutput;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionColumnValue;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.jdbc.partition.rule.PartitionRule;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by xuji on 2017/7/10.
 * 逻辑表路由实现类
 */
public class DefaultTableRouter implements TableRouter {

    protected final LogicDbConfig logicDbConfig;

    private final SQLStatement sqlStatement;

    protected final ExprConditionalSqlTable exprSqlTable;

    public DefaultTableRouter(LogicDbConfig logicDbConfig, SQLStatement sqlStatement, ExprConditionalSqlTable exprSqlTable) {
        this.logicDbConfig = logicDbConfig;
        this.sqlStatement = sqlStatement;
        this.exprSqlTable = exprSqlTable;
    }

    public Map<Partition, SqlTablePartitionSql> route(RouteEvent routeEvent) throws SQLException {
        ExprConditionalSqlTable exprConditionalSqlTable =  exprSqlTable;

        Set<String> partitionColumnNames = routeEvent.getLogicTableConfig().getPartitionColumnNames();

        Map<SqlRefer, SqlExprEvaluator> columnValueMap = exprConditionalSqlTable.getColumnValueMap();
        Map<SqlRefer, SqlExprEvaluator> partitionColumnValueMap = new HashMap<>();
        if (columnValueMap != null) {
            columnValueMap.entrySet().stream().filter(entry -> partitionColumnNames.contains(entry.getKey().getName().toLowerCase())).forEach(entry -> {
                partitionColumnValueMap.put(entry.getKey(), entry.getValue());
            });
        }
        Map<List<SQLExpr>, SQLInListEvaluator> sqlInValuesMap = exprConditionalSqlTable.getColumnInValueListMap();
        Map<List<SQLExpr>, SQLInListEvaluator> partitionColumnInValueListMap = new HashMap<>();
        sqlInValuesMap.forEach((listKey, sqlInListEvaluator) -> {
            int size = listKey.size();
            for (int i = 0; i < size; i++) {
                SQLExpr sqlExpr = listKey.get(i);
                if (sqlExpr instanceof SqlRefer) {
                    SqlRefer sqlRefer = (SqlRefer) sqlExpr;
                    String columnName = sqlRefer.getName().toLowerCase();
                    if (partitionColumnNames.contains(columnName)) {
                        partitionColumnInValueListMap.put(listKey, sqlInListEvaluator);
                        break;
                    }
                }
            }
        });

        if (partitionColumnValueMap.isEmpty() && partitionColumnInValueListMap.isEmpty()) {
            //TODO 全分区sql
            return allPartitionRoute(routeEvent);
        }
        if (!partitionColumnInValueListMap.isEmpty()) {
            return columnInValueListRoute(routeEvent, partitionColumnValueMap, partitionColumnInValueListMap);
        } else {
            return columnEqualsRoute(routeEvent, partitionColumnValueMap);
        }
    }

    public Map<Partition, SqlTablePartitionSql> allPartitionRoute(RouteEvent routeEvent) {
        Map<Partition, SqlTablePartitionSql> sqlTablePartitions = new ConcurrentSkipListMap<>(routeEvent.getLogicTableConfig().getPartitionSortType().getComparator());
        LogicTableConfig logicTableConfig = routeEvent.getLogicTableConfig();
        SortedSet<Partition> partitions = logicTableConfig.getPartitions();
        return sqlTablePartitions;
    }
    /**
     * 分区路由核心方法
     * 没有in表达式 只有 列=value的 分区条件
     *
     * @throws SQLException
     */
    protected Map<Partition, SqlTablePartitionSql> columnEqualsRoute(RouteEvent routeEvent, Map<SqlRefer, SqlExprEvaluator> partitionColumnValueMap) throws SQLException {
        LogicTableConfig logicTableConfig = routeEvent.getLogicTableConfig();
        PartitionRule partitionRule = logicTableConfig.getPartitionRule();
        TreeSet<PartitionColumnValue> partitionColumnValueTreeSet = new TreeSet<>();
        //TODO 数据迁移时老区新区 周新区 update时策略 新区老区双写，表格主键的获取
        PartitionEvent partitionEvent = new PartitionEvent(logicTableConfig.getLogicTableName(), routeEvent.getEventType(), logicTableConfig.getPartitionSortType(),
            logicTableConfig.getPartitionColumnConfigs());
        partitionEvent.setPartitions(logicTableConfig.getPartitions());
        partitionEvent.setPhysicDbs(logicTableConfig.getPhysicDbs());
        SqlExprEvalContext sqlExprEvalContext = new SqlExprEvalContext(routeEvent.getLogicSqlParameterHolder());

        for (Map.Entry<SqlRefer, SqlExprEvaluator> entry2 : partitionColumnValueMap.entrySet()) {
            Object value = entry2.getValue().eval(sqlExprEvalContext, null);
            SqlColumnValue columnValueInner = new SqlColumnValue(entry2.getKey().getName(), value);
            partitionColumnValueTreeSet.add(columnValueInner);
        }
        SortedSet<Partition> partitions = partitionRule.selectPartitions(partitionEvent, partitionColumnValueTreeSet);
        Map<Partition, SqlTablePartitionSql> sqlTablePartitions = new ConcurrentSkipListMap<>(routeEvent.getLogicTableConfig().getPartitionSortType().getComparator());
        if (partitions == null || partitions.isEmpty()) {
            return sqlTablePartitions;
        }
        for (Partition partition : partitions) {
            SqlTablePartition sqlTablePartition = new SqlTablePartition(exprSqlTable, partition);
            sqlTablePartition.setTotalPartitions(partitions.size());
            StringBuilder sqlSb = new StringBuilder();
            MySqlPartitionSqlOutput mySqlPartitionSqlOutput = new MySqlPartitionSqlOutput(sqlSb, logicDbConfig, routeEvent, sqlTablePartition);
            sqlStatement.accept(mySqlPartitionSqlOutput);
            List<SqlParameter> newSqlParameters = mySqlPartitionSqlOutput.getSqlParameterList();
            String sql = sqlSb.toString();
            sqlTablePartitions.put(partition, new SqlTablePartitionSql(sql, newSqlParameters));
        }
        return sqlTablePartitions;
    }

    /**
     * 分区路由核心方法
     * 多个in表达式的列作为分库分表字段  将会对每个column的in的每个value做全排列组合，最后取交集的分区结果
     *
     * @throws SQLException
     */
    protected Map<Partition, SqlTablePartitionSql> columnInValueListRoute(RouteEvent routeEvent, Map<SqlRefer, SqlExprEvaluator> partitionColumnValueMap,
        Map<List<SQLExpr>, SQLInListEvaluator> partitionColumnInValueListMap) throws SQLException {
        LogicTableConfig logicTableConfig = routeEvent.getLogicTableConfig();
        PartitionRule partitionRule = logicTableConfig.getPartitionRule();
        SqlExprEvalContext sqlExprEvalContext = new SqlExprEvalContext(routeEvent.getLogicSqlParameterHolder());
        ColumnInValueListRouter columnInValueListRouter = new ColumnInValueListRouter(sqlExprEvalContext, partitionColumnInValueListMap, partitionColumnValueMap);

        PartitionEvent partitionEvent = new PartitionEvent(logicTableConfig.getLogicTableName(), routeEvent.getEventType(), logicTableConfig.getPartitionSortType(),
            logicTableConfig.getPartitionColumnConfigs());

        partitionEvent.setPartitions(logicTableConfig.getPartitions());
        partitionEvent.setPhysicDbs(logicTableConfig.getPhysicDbs());
        Map<Partition, Map<SQLInListExpr, List<Object[]>>> partitionColumnsMap = new HashMap<>();
        while (columnInValueListRouter.next()) {
            TreeSet<PartitionColumnValue> partitionColumnValueTreeSet = columnInValueListRouter.getCurrentPartitionColumnValues();
            SortedSet<Partition> partitions = partitionRule.selectPartitions(partitionEvent, partitionColumnValueTreeSet);
            if (partitions.isEmpty()) {
                continue;
            }
            Map<List<SQLExpr>, Pair<SQLInListExpr, Object[]>> map = columnInValueListRouter.getCurrentRowColumnValues();
            for (Partition partition : partitions) {
                Map<SQLInListExpr, List<Object[]>> pairList = partitionColumnsMap.get(partition);
                if (pairList == null) {
                    pairList = new LinkedHashMap<>();
                    partitionColumnsMap.put(partition, pairList);
                }
                for (Pair<SQLInListExpr, Object[]> pair : map.values()) {
                    List<Object[]> list = pairList.get(pair.getLeft());
                    Object[] rowValue = pair.getRight();
                    boolean add = true;
                    for (int k = 0; k < list.size(); k++) {
                        Object[] exits = list.get(k);
                        if (Arrays.equals(rowValue, exits)) {
                            add = false;
                        }
                    }
                    if (add) {
                        list.add(rowValue);
                    }
                }
            }
        }
        Map<Partition, SqlTablePartitionSql> sqlTablePartitions = new ConcurrentSkipListMap<>(routeEvent.getLogicTableConfig().getPartitionSortType().getComparator());
        for (Map.Entry<Partition, Map<SQLInListExpr, List<Object[]>>> entry : partitionColumnsMap.entrySet()) {
            Partition partition = entry.getKey();
            SqlTablePartition sqlTablePartition = new SqlTablePartition(exprSqlTable, partition);
            sqlTablePartition.setTotalPartitions(partitionColumnsMap.size());
            sqlTablePartition.getSubInListExpr().putAll(entry.getValue());
            StringBuilder sqlSb = new StringBuilder();
            MySqlPartitionSqlOutput mySqlPartitionSqlOutput = new MySqlPartitionSqlOutput(sqlSb, logicDbConfig, routeEvent, sqlTablePartition);
            sqlStatement.accept(mySqlPartitionSqlOutput);
            List<SqlParameter> newSqlParameters = mySqlPartitionSqlOutput.getSqlParameterList();
            String sql = sqlSb.toString();
            sqlTablePartitions.put(partition, new SqlTablePartitionSql(sql, newSqlParameters));
        }
        return sqlTablePartitions;

    }


}
