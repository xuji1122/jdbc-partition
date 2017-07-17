package org.the.force.jdbc.partition.engine.executor.dml;

import org.the.force.jdbc.partition.engine.parameter.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluatorFactory;
import org.the.force.jdbc.partition.engine.executor.BatchAbleSqlExecution;
import org.the.force.jdbc.partition.engine.executor.physic.LinedParameters;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicTableExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PreparedPhysicSqlExecutor;
import org.the.force.jdbc.partition.engine.sqlelements.sqltable.InsertSqlTable;
import org.the.force.jdbc.partition.engine.sqlelements.SqlRefer;
import org.the.force.jdbc.partition.engine.sqlelements.SqlTablePartitionSql;
import org.the.force.jdbc.partition.engine.router.InsertTableRouter;
import org.the.force.jdbc.partition.engine.router.RouteEvent;
import org.the.force.jdbc.partition.engine.router.TableRouter;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.table.SubQueryResetParser;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.exception.UnsupportedSqlClauseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/5/18.
 */
public class InsertExecutor implements BatchAbleSqlExecution {

    private static Log logger = LogFactory.getLog(InsertExecutor.class);

    private final LogicDbConfig logicDbConfig;

    private final SQLInsertStatement originStatement;//输出物理sql的模板，本身不会变，但是outputVisitor会重写输出sql的逻辑

    private final InsertSqlTable sqlTable;//只对应一个逻辑表,有临时状态

    private final TableRouter tableRouter;



    public InsertExecutor(LogicDbConfig logicDbConfig, SQLInsertStatement sqlStatement) throws Exception {
        this.logicDbConfig = logicDbConfig;
        this.originStatement = sqlStatement;
        if (originStatement.getQuery() != null) {
            throw new UnsupportedSqlClauseException("originStatement.getQuery() != null");
        }
        sqlTable = new InsertSqlTable(logicDbConfig, originStatement.getTableSource());
        new SqlTableReferParser(logicDbConfig, originStatement, sqlTable);
        new SubQueryResetParser(logicDbConfig, originStatement);
        visitColumns();
        tableRouter = new InsertTableRouter(logicDbConfig, originStatement, sqlTable);

    }

    protected void visitColumns() throws SQLException {
        List<SQLExpr> columnExprs = originStatement.getColumns();
        for (int i = 0; i < columnExprs.size(); i++) {
            SQLExpr sqlExpr = columnExprs.get(i);
            if (!(sqlExpr instanceof SQLName)) {
                throw new SqlParseException("insert必须指定column");
            }
            SqlRefer sqlRefer = new SqlRefer((SQLName) sqlExpr);
            sqlTable.getColumnMap().put(i, sqlRefer);
        }

        SQLInsertStatement.ValuesClause valuesClause = originStatement.getValuesList().get(0);
        List<SQLExpr> list = valuesClause.getValues();
        SqlExprEvaluatorFactory factory = logicDbConfig.getSqlExprEvaluatorFactory();
        for (int i = 0; i < list.size(); i++) {
            sqlTable.getEvaluatorMap().put(i, factory.matchSqlExprEvaluator(list.get(i)));
        }
    }

    public final void addSqlLine(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {

        LogicTableConfig[] configPair = logicDbConfig.getLogicTableManager(sqlTable.getTableName()).getLogicTableConfig();
        LogicTableConfig logicTableConfig = configPair[0];
        //TODO 数据迁移时老区新区 周新区  有on duplacate key update时策略 新区老区双写，表格主键的获取
        //TODO no partition的处理 跳过
        //复制模式，修改其中的部分
        //key为physicTableName
        int lineNumber = logicSqlParameterHolder.getLineNumber();
        RouteEvent routeEvent = new RouteEvent(logicTableConfig, PartitionEvent.EventType.INSERT, logicSqlParameterHolder);
        Map<Partition, SqlTablePartitionSql> subsMap = tableRouter.route(routeEvent);
        for (Map.Entry<Partition, SqlTablePartitionSql> entry : subsMap.entrySet()) {
            SqlTablePartitionSql partitionSql = entry.getValue();
            Partition sqlTablePartition = entry.getKey();
            String physicDbName = logicDbConfig.getPhysicDbConfig(sqlTablePartition.getPhysicDbName()).getPhysicDbName();
            PhysicTableExecutor sqlExecuteRouter = physicDbExecutor.get(physicDbName);
            PreparedPhysicSqlExecutor preparedSqlDbExecuteSql = sqlExecuteRouter.get(partitionSql.getSql());
            if (preparedSqlDbExecuteSql == null) {
                preparedSqlDbExecuteSql = new PreparedPhysicSqlExecutor(partitionSql.getSql(), physicDbName);
                sqlExecuteRouter.add(preparedSqlDbExecuteSql);
            }
            preparedSqlDbExecuteSql.addParameters(new LinedParameters(lineNumber, partitionSql.getSqlParameters()));
        }
    }

    @Override
    public String toString() {
        return "InsertExecutor{" + "sqlTable=" + sqlTable + '}';
    }
}
