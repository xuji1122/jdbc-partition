package org.the.force.jdbc.partition.engine.executor.plan.dml;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.physic.LinedParameters;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicTableExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PreparedPhysicSqlExecutor;
import org.the.force.jdbc.partition.engine.executor.plan.BatchAbleSqlPlan;
import org.the.force.jdbc.partition.engine.parameter.SqlParameter;
import org.the.force.jdbc.partition.engine.parser.SqlValueEvalContext;
import org.the.force.jdbc.partition.engine.parser.elements.ExprSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumnValue;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTablePartition;
import org.the.force.jdbc.partition.engine.parser.output.MySqlPartitionSqlOutput;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlNameParser;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlTableColumnsParser;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlTableParser;
import org.the.force.jdbc.partition.engine.parser.value.SqlValue;
import org.the.force.jdbc.partition.engine.parser.value.SqlValueFunction;
import org.the.force.jdbc.partition.engine.parser.value.SqlValueFunctionMatcher;
import org.the.force.jdbc.partition.exception.PartitionConfigException;
import org.the.force.jdbc.partition.exception.UnsupportedSqlClauseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionColumnValue;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.jdbc.partition.rule.PartitionRule;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;
import org.the.force.thirdparty.druid.sql.visitor.SQLEvalVisitor;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by xuji on 2017/5/18.
 */
public class InsertPlan implements BatchAbleSqlPlan {

    private static Log logger = LogFactory.getLog(InsertPlan.class);

    private final LogicDbConfig logicDbConfig;

    private final SQLInsertStatement originStatement;//输出物理sql的模板，本身不会变，但是outputVisitor会重写输出sql的逻辑


    protected final ExprSqlTable sqlTable;//只对应一个逻辑表,有临时状态

    //protected LogicTableConfig logicTableConfig;//不可变对象

    protected PartitionEvent.EventType eventType;//

    //protected Map<Integer, SqlColumnValue> partitionColumnMap;//有状态，不支持并发

    //private SQLExprTableSource originSqlExprTableSource;//不可变

    //private List<SQLInsertStatement.ValuesClause> valuesClauseList = new ArrayList<>();//备份不变,originStatement可以清空之


    public InsertPlan(LogicDbConfig logicDbConfig, SQLInsertStatement sqlStatement) throws Exception {
        this.logicDbConfig = logicDbConfig;
        this.originStatement = sqlStatement;
        if (originStatement.getQuery() != null) {
            throw new UnsupportedSqlClauseException("originStatement.getQuery() != null");
        }
        sqlTable = SqlTableParser.getSQLExprTable(originStatement.getTableSource(), logicDbConfig);
        if (sqlTable == null) {
            throw new RuntimeException("exprSqlTable == null");
        }
        SqlTableColumnsParser parser = new SqlTableColumnsParser(originStatement.getTableSource());
        sqlTable.setAlias(parser.getTableAlias());
    }

    protected Map<Integer, SqlColumnValue> visitColumns(LogicTableConfig logicTableConfig, List<SQLExpr> columnExprs) throws SQLException {

        Map<Integer, SqlColumnValue> map = new HashMap<>();
        for (int i = 0; i < columnExprs.size(); i++) {
            SQLExpr sqlExpr = columnExprs.get(i);
            String columnName = SqlNameParser.getSQLIdentifier(sqlExpr);
            if (logicTableConfig.getPartitionColumnNames().contains(columnName)) {
                map.put(i, new SqlColumnValue(columnName));
            }
        }
        if (map.isEmpty()) {
            //TODO 异常处理
            throw new PartitionConfigException("insert语句必须包含分库分表列");
        }
        return map;
    }

    protected SqlTablePartition visit(LogicTableConfig logicTableConfig, SQLInsertStatement.ValuesClause valuesClause, SqlValueEvalContext sqlValueEvalContext) throws Exception {
        PartitionEvent partitionEvent = new PartitionEvent(logicTableConfig.getLogicTableName(), eventType,logicTableConfig.getPartitionSortType(), logicTableConfig.getPartitionColumnConfigs());
        partitionEvent.setPartitions(logicTableConfig.getPartitions());
        partitionEvent.setPhysicDbs(logicTableConfig.getPhysicDbs());
        SqlValueFunctionMatcher sqlValueFunctionMatcher = SqlValueFunctionMatcher.getSingleton();
        Map<Integer, SqlColumnValue> partitionColumnMap = visitColumns(logicTableConfig, originStatement.getColumns());
        List<SQLExpr> sqlExprList = valuesClause.getValues();
        for (int i = 0; i < sqlExprList.size(); i++) {
            SQLExpr sqlExpr = sqlExprList.get(i);
            SqlColumnValue sqlColumnValue = partitionColumnMap.get(i);
            if (sqlColumnValue != null) {
                SqlValueFunction sqlValueFunction = sqlValueFunctionMatcher.matchSqlValueFunction(sqlExpr, sqlValueEvalContext);
                if (sqlValueFunction == null) {
                    //TODO 异常处理
                    continue;
                }
                SqlValue sqlValue = sqlValueFunction.getSqlValue(sqlExpr, sqlValueEvalContext);
                if (sqlValue == null) {
                    //TODO 异常处理
                    continue;
                }
                Object value = sqlValue.getValue();
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
        if (partitions.isEmpty()) {

        }
        return new SqlTablePartition(sqlTable, partitions.iterator().next());
    }


    public final void addSqlLine(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {
        SqlValueEvalContext sqlValueEvalContext = new SqlValueEvalContext(logicDbConfig, logicSqlParameterHolder);

        LogicTableConfig[] configPair = logicDbConfig.getLogicTableManager(sqlTable.getTableName()).getLogicTableConfig();
        LogicTableConfig logicTableConfig = configPair[0];
        //TODO 数据迁移时老区新区 周新区  有on duplacate key update时策略 新区老区双写，表格主键的获取
        //TODO no partition的处理 跳过

        //复制模式，修改其中的部分
        //key为physicTableName
        int lineNumber = logicSqlParameterHolder.getLineNumber();
        Map<SqlTablePartition, List<SQLInsertStatement.ValuesClause>> subsMap = new LinkedHashMap<>();
        List<SQLInsertStatement.ValuesClause> valuesClauseList = originStatement.getValuesList();
        for (SQLInsertStatement.ValuesClause valuesClause : valuesClauseList) {
            SqlTablePartition sqlTablePartition = visit(logicTableConfig, valuesClause, sqlValueEvalContext);
            List<SQLInsertStatement.ValuesClause> pair = subsMap.get(sqlTablePartition);
            if (pair == null) {
                pair = new ArrayList<>();
                subsMap.put(sqlTablePartition, pair);
            }
            pair.add(valuesClause);
        }

        for (Map.Entry<SqlTablePartition, List<SQLInsertStatement.ValuesClause>> entry : subsMap.entrySet()) {
            List<SQLInsertStatement.ValuesClause> valuesClauses = entry.getValue();
            SqlTablePartition sqlTablePartition = entry.getKey();
            StringBuilder sqlSb = new StringBuilder();
            MySqlPartitionSqlOutput mySqlPartitionSqlOutput = new MySqlPartitionSqlOutput(sqlSb, sqlTablePartition, logicSqlParameterHolder);
            mySqlPartitionSqlOutput.getValuesClauses().addAll(valuesClauses);
            originStatement.accept(mySqlPartitionSqlOutput);
            List<SqlParameter> list = mySqlPartitionSqlOutput.getSqlParameterList();
            String sql = sqlSb.toString();
            String physicDbName = logicDbConfig.getPhysicDbConfig(sqlTablePartition.getPartition().getPhysicDbName()).getPhysicDbName();
            PhysicTableExecutor sqlExecuteRouter = physicDbExecutor.get(physicDbName);
            PreparedPhysicSqlExecutor preparedSqlDbExecuteSql = sqlExecuteRouter.get(sql);
            if (preparedSqlDbExecuteSql == null) {
                preparedSqlDbExecuteSql = new PreparedPhysicSqlExecutor(sql, physicDbName);
                sqlExecuteRouter.add(preparedSqlDbExecuteSql);
            }
            preparedSqlDbExecuteSql.addParameters(new LinedParameters(lineNumber, list));
        }
    }

    @Override
    public String toString() {
        return "InsertPlan{" + "sqlTable=" + sqlTable + ", eventType=" + eventType + '}';
    }
}
