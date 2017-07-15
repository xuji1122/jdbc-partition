package org.the.force.jdbc.partition.engine.parser.router;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.executor.eval.SqlExprEvalFunction;
import org.the.force.jdbc.partition.engine.parameter.SqlParameter;
import org.the.force.jdbc.partition.engine.executor.eval.SqlValueEvalContext;
import org.the.force.jdbc.partition.engine.parser.elements.ExprSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumn;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumnValue;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTablePartition;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTablePartitionSql;
import org.the.force.jdbc.partition.engine.parser.output.MySqlPartitionSqlOutput;
import org.the.force.jdbc.partition.engine.executor.eval.SqlExprEvalFunctionFactory;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionColumnValue;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.jdbc.partition.rule.PartitionRule;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;
import org.the.force.thirdparty.druid.sql.visitor.SQLEvalVisitor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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

    protected final ExprSqlTable exprSqlTable;

    private final SqlValueEvalContext sqlValueEvalContext;

    public DefaultTableRouter(LogicDbConfig logicDbConfig, ExprSqlTable exprSqlTable, SqlValueEvalContext sqlValueEvalContext) {
        this.logicDbConfig = logicDbConfig;
        this.exprSqlTable = exprSqlTable;
        this.sqlValueEvalContext = sqlValueEvalContext;
    }

    public Map<Partition, SqlTablePartitionSql> route(RouteEvent routeEvent) throws SQLException {
        if (routeEvent.isInsertInto()) {
            return insertIntoRoute(routeEvent, routeEvent.getPartitionColumnMap(), routeEvent.getValuesClauseList());
        }
        Map<SqlColumn, SQLExpr> partitionColumnValueMap = new HashMap<>();
        Map<SqlColumn, SQLInListExpr> partitionSqlInValuesMap = new HashMap<>();
        Set<String> partitionColumnNames = routeEvent.getLogicTableConfig().getPartitionColumnNames();
        Map<SqlColumn, SQLExpr> columnValueMap = routeEvent.getColumnValueMap();
        Map<SqlColumn, SQLInListExpr> sqlInValuesMap = routeEvent.getSqlInValuesMap();
        if (columnValueMap != null) {
            columnValueMap.entrySet().stream().filter(entry -> partitionColumnNames.contains(entry.getKey().getColumnName().toLowerCase())).forEach(entry -> {
                partitionColumnValueMap.put(entry.getKey(), entry.getValue());
            });
        }
        if (sqlInValuesMap != null) {
            sqlInValuesMap.entrySet().stream().filter(entry -> partitionColumnNames.contains(entry.getKey().getColumnName().toLowerCase())).forEach(entry -> {
                partitionSqlInValuesMap.put(entry.getKey(), entry.getValue());
            });
        }
        if (partitionColumnValueMap.isEmpty() && partitionSqlInValuesMap.isEmpty()) {
            //TODO 全分区sql
            return allPartitionSql(routeEvent);
        }
        if (!partitionSqlInValuesMap.isEmpty()) {
            return columnInListPartition(routeEvent, partitionColumnValueMap, partitionSqlInValuesMap);
        } else {
            return columnEqualsPartition(routeEvent, partitionColumnValueMap);
        }
    }

    public Map<Partition, SqlTablePartitionSql> allPartitionSql(RouteEvent routeEvent) {
        Map<Partition, SqlTablePartitionSql> sqlTablePartitions = new ConcurrentSkipListMap<>(routeEvent.getLogicTableConfig().getPartitionSortType().getComparator());
        LogicTableConfig logicTableConfig = routeEvent.getLogicTableConfig();
        SortedSet<Partition> partitions = logicTableConfig.getPartitions();
        return sqlTablePartitions;
    }

    /**
     * 分区路由核心方法
     * 将sql路由到所有分区
     */
    protected Map<Partition, SqlTablePartitionSql> insertIntoRoute(RouteEvent routeEvent, Map<Integer, SqlColumnValue> partitionColumnMap,
        List<SQLInsertStatement.ValuesClause> valuesClauseList) throws SQLException {
        SqlExprEvalFunctionFactory sqlExprEvalFunctionFactory = SqlExprEvalFunctionFactory.getSingleton();
        LogicTableConfig logicTableConfig = routeEvent.getLogicTableConfig();

        PartitionEvent partitionEvent = new PartitionEvent(logicTableConfig.getLogicTableName(), routeEvent.getEventType(), logicTableConfig.getPartitionSortType(),
            logicTableConfig.getPartitionColumnConfigs());
        partitionEvent.setPartitions(logicTableConfig.getPartitions());
        partitionEvent.setPhysicDbs(logicTableConfig.getPhysicDbs());

        Map<Partition, SqlTablePartition> subsMap = new ConcurrentSkipListMap<>(routeEvent.getLogicTableConfig().getPartitionSortType().getComparator());
        for (SQLInsertStatement.ValuesClause valuesClause : valuesClauseList) {
            List<SQLExpr> sqlExprList = valuesClause.getValues();
            for (int i = 0; i < sqlExprList.size(); i++) {
                SQLExpr sqlExpr = sqlExprList.get(i);
                SqlColumnValue sqlColumnValue = partitionColumnMap.get(i);
                if (sqlColumnValue != null) {
                    SqlExprEvalFunction sqlValueFunction = sqlExprEvalFunctionFactory.matchSqlValueFunction(sqlExpr);
                    if (sqlValueFunction == null) {
                        //TODO 异常处理
                        continue;
                    }
                    Object value = sqlValueFunction.getValue(sqlValueEvalContext,routeEvent.getLogicSqlParameterHolder(),null);
                    if (value == null) {
                        //TODO 异常处理
                        continue;
                    }
                    if (SQLEvalVisitor.EVAL_VALUE_NULL == value) {
                        sqlColumnValue.setValue(null);
                    } else {
                        sqlColumnValue.setValue(value);
                    }
                }
            }
            PartitionRule partitionRule = logicTableConfig.getPartitionRule();
            TreeSet<PartitionColumnValue> partitionColumnValueTreeSet = new TreeSet<>();
            for (Map.Entry<Integer, SqlColumnValue> entry : partitionColumnMap.entrySet()) {
                SqlColumnValue sqlColumnValue = entry.getValue();
                if (sqlColumnValue.getValue() != null) {
                    partitionColumnValueTreeSet.add(sqlColumnValue);
                }
            }
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
            routeEvent.getSqlStatement().accept(mySqlPartitionSqlOutput);
            List<SqlParameter> list = mySqlPartitionSqlOutput.getSqlParameterList();
            String sql = sqlSb.toString();
            sqlTablePartitions.put(entry.getKey(), new SqlTablePartitionSql(sql, list));
        }
        return sqlTablePartitions;
    }



    /**
     * 分区路由核心方法
     * 多个in表达式的列作为分库分表字段  将会对每个column的in的每个value做全排列组合，最后取交集的分区结果
     *
     * @throws SQLException
     */
    protected Map<Partition, SqlTablePartitionSql> columnInListPartition(RouteEvent routeEvent, Map<SqlColumn, SQLExpr> partitionColumnValueMap,
        Map<SqlColumn, SQLInListExpr> partitionColumnSqlInValuesMap) throws SQLException {
        LogicTableConfig logicTableConfig = routeEvent.getLogicTableConfig();
        SqlExprEvalFunctionFactory sqlExprEvalFunctionFactory = SqlExprEvalFunctionFactory.getSingleton();
        PartitionRule partitionRule = logicTableConfig.getPartitionRule();
        /**
         * 根据每个in表达式的column 分别做分区  Column-->Partition
         */
        Map<SqlColumn, Map<Partition, Pair<SQLInListExpr, List<SQLExpr>>>> columnPartitionResultMap = new HashMap<>();

        PartitionEvent partitionEvent = new PartitionEvent(logicTableConfig.getLogicTableName(), routeEvent.getEventType(), logicTableConfig.getPartitionSortType(),
            logicTableConfig.getPartitionColumnConfigs());
        partitionEvent.setPartitions(logicTableConfig.getPartitions());
        partitionEvent.setPhysicDbs(logicTableConfig.getPhysicDbs());
        //TODO no partition的处理 跳过
        for (Map.Entry<SqlColumn, SQLInListExpr> entry1 : partitionColumnSqlInValuesMap.entrySet()) {
            //指定的column,一个column对象唯一一个SQLInListExpr对象
            SqlColumn sqlColumn = entry1.getKey();
            Map<Partition, Pair<SQLInListExpr, List<SQLExpr>>> partitionInListMap = new HashMap<>();
            columnPartitionResultMap.put(sqlColumn, partitionInListMap);
            List<SQLExpr> sqlExprList = entry1.getValue().getTargetList();//不变的的list
            for (SQLExpr sqlExpr : sqlExprList) {// in的每一个选项
                TreeSet<PartitionColumnValue> partitionColumnValueTreeSet = new TreeSet<>();
                SqlColumnValue columnValueOuter = new SqlColumnValue(entry1.getKey().getColumnName());
                Object value = sqlExprEvalFunctionFactory.matchSqlValueFunction(sqlExpr).getValue(sqlValueEvalContext,routeEvent.getLogicSqlParameterHolder(),null);
                columnValueOuter.setValue(value);
                partitionColumnValueTreeSet.add(columnValueOuter);
                for (Map.Entry<SqlColumn, SQLExpr> entry2 : partitionColumnValueMap.entrySet()) {//等于的选项全部拿出来
                    SqlColumnValue columnValueInner = new SqlColumnValue(entry2.getKey().getColumnName());
                    value = sqlExprEvalFunctionFactory.matchSqlValueFunction(entry2.getValue()).getValue( sqlValueEvalContext,routeEvent.getLogicSqlParameterHolder(),null);
                    columnValueInner.setValue(value);
                    partitionColumnValueTreeSet.add(columnValueInner);
                }
                SortedSet<Partition> partitions = partitionRule.selectPartitions(partitionEvent, partitionColumnValueTreeSet);
                if (partitions.isEmpty()) {
                    continue;
                }

                for (Partition partition : partitions) {
                    Pair<SQLInListExpr, List<SQLExpr>> list = partitionInListMap.get(partition);
                    if (list == null) {
                        list = new Pair<>(entry1.getValue(), new ArrayList<SQLExpr>());
                        partitionInListMap.put(partition, list);
                    }
                    list.getRight().add(sqlExpr);
                }
            }
        }
        /**
         *   result 每一列的分区结果  取交集  reduce Column-->Partition  变成 Partition-->Column
         *   Map<SqlColumn, Map<Partition, Triple<SQLInListExpr, SQLInListExpr,List<SQLExpr>>>>  ->  Map<Partition, List<Triple<SQLInListExpr, SQLInListExpr, List<SQLExpr>>>>
         */

        //结果集的结构，某个patition，使用了哪些列(SQLInListExpr)的哪些value (List<SQLExpr>)
        Map<Partition, List<Pair<SQLInListExpr, List<SQLExpr>>>> partitionColumnsMap = new HashMap<>();
        //统计某个Partition在所有SqlColumn中出现的次数，用于取交集
        Map<Partition, Integer> partitionCount = new HashMap<>();
        for (Map<Partition, Pair<SQLInListExpr, List<SQLExpr>>> value : columnPartitionResultMap.values()) {
            if (value.isEmpty()) {
                continue;
            }
            for (Map.Entry<Partition, Pair<SQLInListExpr, List<SQLExpr>>> entry : value.entrySet()) {
                Partition partition = entry.getKey();
                List<Pair<SQLInListExpr, List<SQLExpr>>> list = partitionColumnsMap.get(partition);
                if (list == null) {
                    list = new ArrayList<>();
                    partitionColumnsMap.put(partition, list);
                }
                list.add(entry.getValue());
                Integer count = partitionCount.get(partition);
                if (count == null) {
                    partitionCount.put(partition, 1);
                } else {
                    partitionCount.put(partition, count + 1);
                }
            }
        }
        //去除 假条件
        for (Map.Entry<Partition, Integer> entry : partitionCount.entrySet()) {
            if (entry.getValue() == null || entry.getValue() < columnPartitionResultMap.size()) {
                partitionColumnsMap.remove(entry.getKey());
            }
        }

        Map<Partition, SqlTablePartitionSql> sqlTablePartitions = new ConcurrentSkipListMap<>(routeEvent.getLogicTableConfig().getPartitionSortType().getComparator());
        for (Map.Entry<Partition, List<Pair<SQLInListExpr, List<SQLExpr>>>> entry : partitionColumnsMap.entrySet()) {
            Partition partition = entry.getKey();
            SqlTablePartition sqlTablePartition = new SqlTablePartition(exprSqlTable, partition);
            sqlTablePartition.setTotalPartitions(partitionColumnsMap.size());
            sqlTablePartition.getSubInListExpr().addAll(entry.getValue());

            StringBuilder sqlSb = new StringBuilder();
            MySqlPartitionSqlOutput mySqlPartitionSqlOutput = new MySqlPartitionSqlOutput(sqlSb, logicDbConfig, routeEvent, sqlTablePartition);
            routeEvent.getSqlStatement().accept(mySqlPartitionSqlOutput);
            List<SqlParameter> newSqlParameters = mySqlPartitionSqlOutput.getSqlParameterList();
            String sql = sqlSb.toString();
            sqlTablePartitions.put(partition, new SqlTablePartitionSql(sql, newSqlParameters));
        }
        return sqlTablePartitions;

    }

    /**
     * 分区路由核心方法
     * 没有in表达式 只有 列=value的 分区条件
     *
     * @throws SQLException
     */
    protected Map<Partition, SqlTablePartitionSql> columnEqualsPartition(RouteEvent routeEvent, Map<SqlColumn, SQLExpr> partitionColumnValueMap) throws SQLException {
        LogicTableConfig logicTableConfig = routeEvent.getLogicTableConfig();
        SqlExprEvalFunctionFactory sqlExprEvalFunctionFactory = SqlExprEvalFunctionFactory.getSingleton();
        PartitionRule partitionRule = logicTableConfig.getPartitionRule();
        TreeSet<PartitionColumnValue> partitionColumnValueTreeSet = new TreeSet<>();
        //TODO 数据迁移时老区新区 周新区 update时策略 新区老区双写，表格主键的获取
        PartitionEvent partitionEvent = new PartitionEvent(logicTableConfig.getLogicTableName(), routeEvent.getEventType(), logicTableConfig.getPartitionSortType(),
            logicTableConfig.getPartitionColumnConfigs());
        partitionEvent.setPartitions(logicTableConfig.getPartitions());
        partitionEvent.setPhysicDbs(logicTableConfig.getPhysicDbs());
        for (Map.Entry<SqlColumn, SQLExpr> entry2 : partitionColumnValueMap.entrySet()) {
            SqlColumnValue columnValueInner = new SqlColumnValue(entry2.getKey().getColumnName());
            Object value = sqlExprEvalFunctionFactory.matchSqlValueFunction(entry2.getValue()).getValue( sqlValueEvalContext,routeEvent.getLogicSqlParameterHolder(),null);
            columnValueInner.setValue(value);
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
            routeEvent.getSqlStatement().accept(mySqlPartitionSqlOutput);
            List<SqlParameter> newSqlParameters = mySqlPartitionSqlOutput.getSqlParameterList();
            String sql = sqlSb.toString();
            sqlTablePartitions.put(partition, new SqlTablePartitionSql(sql, newSqlParameters));
        }
        return sqlTablePartitions;
    }

}
