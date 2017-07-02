package org.the.force.jdbc.partition.engine.plan.dml;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;
import org.the.force.thirdparty.druid.sql.visitor.SQLEvalVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicTableExecutor;
import org.the.force.jdbc.partition.engine.parser.MySqlPartitionSqlOutput;
import org.the.force.jdbc.partition.engine.parser.SqlParserContext;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlNameParser;
import org.the.force.jdbc.partition.engine.parser.value.SqlValue;
import org.the.force.jdbc.partition.engine.plan.PhysicSqlPlan;
import org.the.force.jdbc.partition.engine.plan.model.SqlExprTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionColumnValue;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.jdbc.partition.rule.PartitionRule;
import org.the.force.jdbc.partition.engine.executor.physic.LinedParameters;
import org.the.force.jdbc.partition.engine.executor.physic.PreparedPhysicSqlExecutor;
import org.the.force.jdbc.partition.engine.parameter.SqlParameter;
import org.the.force.jdbc.partition.engine.parser.value.SqlValueFunction;
import org.the.force.jdbc.partition.engine.parser.value.SqlValueFunctionMatcher;
import org.the.force.jdbc.partition.engine.plan.model.SqlColumnValue;
import org.the.force.jdbc.partition.engine.plan.model.SqlTablePartition;

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
public class InsertPlan implements PhysicSqlPlan {

    private static Logger logger = LoggerFactory.getLogger(InsertPlan.class);

    private final LogicDbConfig logicDbConfig;

    private final SQLInsertStatement originStatement;//输出物理sql的模板，本身不会变，但是outputVisitor会重写输出sql的逻辑


    protected SqlExprTable sqlTable;//只对应一个逻辑表,有临时状态

    protected LogicTableConfig logicTableConfig;//不可变对象

    protected PartitionEvent.EventType eventType;//

    protected Map<Integer, SqlColumnValue> partitionColumnMap;//有状态，不支持并发

    //private SQLExprTableSource originSqlExprTableSource;//不可变

    //private List<SQLInsertStatement.ValuesClause> valuesClauseList = new ArrayList<>();//备份不变,originStatement可以清空之


    public InsertPlan(LogicDbConfig logicDbConfig, SQLInsertStatement sqlStatement) throws Exception {
        this.logicDbConfig = logicDbConfig;
        this.originStatement = sqlStatement;
        prepare();
    }


    public final void prepare() throws Exception {
        if (originStatement.getQuery() != null) {
            //TODO 不支持的异常
        }
        sqlTable = SqlNameParser.getSQLExprTable(originStatement.getTableSource());
        if (sqlTable == null) {
            throw new RuntimeException("sqlExprTable == null");
        }
        //originSqlExprTableSource = originStatement.getTableSource();
        String logicTableName = sqlTable.getTableName();
        // TODO ` 符号的命名空间
        logicTableConfig = logicDbConfig.getLogicTableManager(logicTableName).getLogicTableConfig()[0];
        //解析columns
        visitColumns(originStatement.getColumns());
        //解析 values
        //valuesClauseList.addAll(originStatement.getValuesList());
        //originStatement.getValuesList().clear();
    }

    protected void visitColumns(List<SQLExpr> columnExprs) throws SQLException {

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
        }
        partitionColumnMap = map;
    }

    protected SqlTablePartition visit(SQLInsertStatement.ValuesClause valuesClause, PartitionEvent partitionEvent, SqlParserContext sqlParserContext) throws Exception {
        SqlValueFunctionMatcher sqlValueFunctionMatcher = SqlValueFunctionMatcher.getSingleton();
        List<SQLExpr> sqlExprList = valuesClause.getValues();
        for (int i = 0; i < sqlExprList.size(); i++) {
            SQLExpr sqlExpr = sqlExprList.get(i);
            SqlColumnValue sqlColumnValue = partitionColumnMap.get(i);
            if (sqlColumnValue != null) {
                SqlValueFunction sqlValueFunction = sqlValueFunctionMatcher.matchSqlValueFunction(sqlExpr, sqlParserContext);
                if (sqlValueFunction == null) {
                    //TODO 异常处理
                    continue;
                }
                SqlValue sqlValue = sqlValueFunction.getSqlValue(sqlExpr, sqlParserContext);
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


    public final void setParameters(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {
        SqlParserContext sqlParserContext = new SqlParserContext(logicDbConfig, logicSqlParameterHolder);
        //TODO 数据迁移时老区新区 周新区  有on duplacate key update时策略 新区老区双写，表格主键的获取
        PartitionEvent partitionEvent = new PartitionEvent(logicTableConfig.getLogicTableName(), eventType, logicTableConfig.getPartitionColumnConfigs());
        partitionEvent.setPartitions(logicTableConfig.getPartitions());
        partitionEvent.setPhysicDbs(logicTableConfig.getPhysicDbs());
        //TODO no partition的处理 跳过
        //复制模式，修改其中的部分
        //key为physicTableName
        int lineNumber = logicSqlParameterHolder.getLineNumber();
        Map<SqlTablePartition, List<SQLInsertStatement.ValuesClause>> subsMap = new LinkedHashMap<>();
        List<SQLInsertStatement.ValuesClause> valuesClauseList = originStatement.getValuesList();
        for (SQLInsertStatement.ValuesClause valuesClause : valuesClauseList) {
            SqlTablePartition sqlTablePartition = visit(valuesClause, partitionEvent, sqlParserContext);
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


    public String toString() {
        return "InsertPlan{" + "sqlExprTable=" + sqlTable + ", partitionColumnMap=" + partitionColumnMap + '}';
    }
}
