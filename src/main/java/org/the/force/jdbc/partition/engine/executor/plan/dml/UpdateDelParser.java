package org.the.force.jdbc.partition.engine.executor.plan.dml;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.physic.LinedParameters;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicTableExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PreparedPhysicSqlExecutor;
import org.the.force.jdbc.partition.engine.parameter.SqlParameter;
import org.the.force.jdbc.partition.engine.parser.SqlValueEvalContext;
import org.the.force.jdbc.partition.engine.parser.TableConditionParser;
import org.the.force.jdbc.partition.engine.parser.elements.ExprSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumn;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumnValue;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTablePartition;
import org.the.force.jdbc.partition.engine.parser.output.MySqlPartitionSqlOutput;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlTableColumnsParser;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlTableParser;
import org.the.force.jdbc.partition.engine.parser.value.SqlValueFunctionMatcher;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionColumnValue;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.jdbc.partition.rule.PartitionRule;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by xuji on 2017/6/1.
 * 更新和删除的解析类
 */
public class UpdateDelParser {

    private final UpdateDelParserAdapter updateDelParserAdapter;

    protected final LogicDbConfig logicDbConfig;

    protected ExprSqlTable exprSqlTable;

    //protected LogicTableConfig logicTableConfig;

    private final PartitionEvent.EventType eventType;

    //private SQLExprTableSource originSqlExprTableSource;//不可变

    private final SQLExpr where;

    private final Map<SqlColumn, SQLExpr> columnValueMap;//静态不变

    private final Map<SqlColumn, SQLInListExpr> sqlInValuesMap;//原始的in表达式，不可变

    public UpdateDelParser(LogicDbConfig logicDbConfig, UpdateDelParserAdapter updateDelParserAdapter) throws Exception {
        this.logicDbConfig = logicDbConfig;
        this.updateDelParserAdapter = updateDelParserAdapter;
        this.eventType = updateDelParserAdapter.getEventType();
        exprSqlTable = SqlTableParser.getSQLExprTable(updateDelParserAdapter.getSQLExprTableSource(), logicDbConfig);
        TableConditionParser tableConditionParser = new TableConditionParser(logicDbConfig, exprSqlTable, updateDelParserAdapter.getCondition());
        this.where = tableConditionParser.getSubQueryResetWhere();
        columnValueMap = tableConditionParser.getCurrentTableColumnValueMap();
        sqlInValuesMap = tableConditionParser.getCurrentTableColumnInValuesMap();
        SqlTableColumnsParser parser = new SqlTableColumnsParser(updateDelParserAdapter.getSQLExprTableSource());
        exprSqlTable.setAlias(parser.getTableAlias());
    }


    public void addSqlLine(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {
        LogicTableConfig[] configPair = logicDbConfig.getLogicTableManager(exprSqlTable.getTableName()).getLogicTableConfig();
        LogicTableConfig logicTableConfig = configPair[0];
        //TODO 数据迁移时老区新区 周新区 update时策略 新区老区双写，表格主键的获取
        Map<SqlColumn, SQLExpr> partitionColumnValueMap = new HashMap<>();
        Map<SqlColumn, SQLInListExpr> partitionSqlInValuesMap = new HashMap<>();
        Set<String> partitionColumnNames = logicTableConfig.getPartitionColumnNames();
        columnValueMap.entrySet().stream().filter(entry -> partitionColumnNames.contains(entry.getKey().getColumnName().toLowerCase())).forEach(entry -> {
            partitionColumnValueMap.put(entry.getKey(), entry.getValue());
        });
        sqlInValuesMap.entrySet().stream().filter(entry -> partitionColumnNames.contains(entry.getKey().getColumnName().toLowerCase())).forEach(entry -> {
            partitionSqlInValuesMap.put(entry.getKey(), entry.getValue());
        });
        if (partitionColumnValueMap.isEmpty() && partitionSqlInValuesMap.isEmpty()) {
            allPartitionSql();
            return;
        }
        if (!partitionSqlInValuesMap.isEmpty()) {
            columnInListPartition(logicTableConfig, partitionColumnValueMap, partitionSqlInValuesMap, physicDbExecutor, logicSqlParameterHolder);
        } else {
            columnEqualsPartition(logicTableConfig, partitionColumnValueMap, physicDbExecutor, logicSqlParameterHolder);
        }
    }

    /**
     * 分区路由核心方法
     * 将sql路由到所有分区
     */
    protected void allPartitionSql() {

    }

    /**
     * 分区路由核心方法
     * 多个in表达式的列作为分库分表字段  将会对每个column的in的每个value做全排列组合，最后取交集的分区结果
     *
     * @param physicDbExecutor
     * @param logicSqlParameterHolder
     * @throws SQLException
     */
    protected void columnInListPartition(LogicTableConfig logicTableConfig, Map<SqlColumn, SQLExpr> partitionColumnValueMap,
        Map<SqlColumn, SQLInListExpr> partitionColumnSqlInValuesMap, PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {

        SqlValueEvalContext sqlValueEvalContext = new SqlValueEvalContext(logicDbConfig, logicSqlParameterHolder);
        SqlValueFunctionMatcher sqlValueFunctionMatcher = SqlValueFunctionMatcher.getSingleton();
        PartitionRule partitionRule = logicTableConfig.getPartitionRule();
        /**
         * 根据每个in表达式的column 分别做分区  Column-->Partition
         */
        Map<SqlColumn, Map<Partition, Pair<SQLInListExpr, List<SQLExpr>>>> columnPartitionResultMap = new HashMap<>();

        PartitionEvent partitionEvent =
            new PartitionEvent(logicTableConfig.getLogicTableName(), eventType, logicTableConfig.getPartitionSortType(), logicTableConfig.getPartitionColumnConfigs());
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
                Object value = sqlValueFunctionMatcher.matchSqlValueFunction(sqlExpr, sqlValueEvalContext).getSqlValue(sqlExpr, sqlValueEvalContext).getValue();
                columnValueOuter.setValue(value);
                partitionColumnValueTreeSet.add(columnValueOuter);
                for (Map.Entry<SqlColumn, SQLExpr> entry2 : partitionColumnValueMap.entrySet()) {//等于的选项全部拿出来
                    SqlColumnValue columnValueInner = new SqlColumnValue(entry2.getKey().getColumnName());
                    value = sqlValueFunctionMatcher.matchSqlValueFunction(entry2.getValue(), sqlValueEvalContext).getSqlValue(entry2.getValue(), sqlValueEvalContext).getValue();
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

        //生成每个分区的sql

        for (Map.Entry<Partition, List<Pair<SQLInListExpr, List<SQLExpr>>>> entry : partitionColumnsMap.entrySet()) {
            Partition partition = entry.getKey();
            SqlTablePartition sqlTablePartition = new SqlTablePartition(exprSqlTable, partition);

            StringBuilder sqlSb = new StringBuilder();
            MySqlPartitionSqlOutput mySqlPartitionSqlOutput = new MySqlPartitionSqlOutput(sqlSb, sqlTablePartition, logicSqlParameterHolder);
            mySqlPartitionSqlOutput.setInListExprCollection(entry.getValue());
            updateDelParserAdapter.getSQLStatement().accept(mySqlPartitionSqlOutput);

            List<SqlParameter> newSqlParameters = mySqlPartitionSqlOutput.getSqlParameterList();

            String sql = sqlSb.toString();
            PhysicTableExecutor sqlExecutorRouter = physicDbExecutor.get(partition.getPhysicDbName());
            PreparedPhysicSqlExecutor preparedSqlDbExecuteSqlExecutor = sqlExecutorRouter.get(sql);
            if (preparedSqlDbExecuteSqlExecutor == null) {
                preparedSqlDbExecuteSqlExecutor = new PreparedPhysicSqlExecutor(sql, partition.getPhysicDbName());
                sqlExecutorRouter.add(preparedSqlDbExecuteSqlExecutor);
            }
            preparedSqlDbExecuteSqlExecutor.addParameters(new LinedParameters(logicSqlParameterHolder.getLineNumber(), newSqlParameters));
        }

    }

    /**
     * 分区路由核心方法
     * 没有in表达式 只有 列=value的 分区条件
     *
     * @param physicDbExecutor
     * @param logicSqlParameterHolder
     * @throws SQLException
     */
    protected void columnEqualsPartition(LogicTableConfig logicTableConfig, Map<SqlColumn, SQLExpr> partitionColumnValueMap, PhysicDbExecutor physicDbExecutor,
        LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
        SqlValueEvalContext sqlValueEvalContext = new SqlValueEvalContext(logicDbConfig, logicSqlParameterHolder);
        SqlValueFunctionMatcher sqlValueFunctionMatcher = SqlValueFunctionMatcher.getSingleton();
        PartitionRule partitionRule = logicTableConfig.getPartitionRule();
        TreeSet<PartitionColumnValue> partitionColumnValueTreeSet = new TreeSet<>();
        //TODO 数据迁移时老区新区 周新区 update时策略 新区老区双写，表格主键的获取
        PartitionEvent partitionEvent =
            new PartitionEvent(logicTableConfig.getLogicTableName(), eventType, logicTableConfig.getPartitionSortType(), logicTableConfig.getPartitionColumnConfigs());
        partitionEvent.setPartitions(logicTableConfig.getPartitions());
        partitionEvent.setPhysicDbs(logicTableConfig.getPhysicDbs());
        for (Map.Entry<SqlColumn, SQLExpr> entry2 : partitionColumnValueMap.entrySet()) {
            SqlColumnValue columnValueInner = new SqlColumnValue(entry2.getKey().getColumnName());
            Object value = sqlValueFunctionMatcher.matchSqlValueFunction(entry2.getValue(), sqlValueEvalContext).getSqlValue(entry2.getValue(), sqlValueEvalContext).getValue();
            columnValueInner.setValue(value);
            partitionColumnValueTreeSet.add(columnValueInner);
        }
        SortedSet<Partition> partitions = partitionRule.selectPartitions(partitionEvent, partitionColumnValueTreeSet);
        if (partitions.isEmpty()) {
            return;
        }
        for (Partition partition : partitions) {
            SqlTablePartition sqlTablePartition = new SqlTablePartition(exprSqlTable, partition);
            StringBuilder sqlSb = new StringBuilder();
            MySqlPartitionSqlOutput mySqlPartitionSqlOutput = new MySqlPartitionSqlOutput(sqlSb, sqlTablePartition, logicSqlParameterHolder);
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
