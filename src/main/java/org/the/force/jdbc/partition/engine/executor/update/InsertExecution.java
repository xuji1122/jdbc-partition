package org.the.force.jdbc.partition.engine.executor.update;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.physic.LinedParameters;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicTableExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.PreparedPhysicSqlExecutor;
import org.the.force.jdbc.partition.engine.executor.BatchAbleSqlExecution;
import org.the.force.jdbc.partition.engine.parser.elements.ExprSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumnValue;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTablePartitionSql;
import org.the.force.jdbc.partition.engine.parser.router.RouteEvent;
import org.the.force.jdbc.partition.engine.parser.router.DefaultTableRouter;
import org.the.force.jdbc.partition.engine.parser.router.TableRouter;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlReferParser;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.exception.PartitionConfigException;
import org.the.force.jdbc.partition.exception.UnsupportedSqlClauseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/5/18.
 */
public class InsertExecution implements BatchAbleSqlExecution {

    private static Log logger = LogFactory.getLog(InsertExecution.class);

    private final LogicDbConfig logicDbConfig;

    private final SQLInsertStatement originStatement;//输出物理sql的模板，本身不会变，但是outputVisitor会重写输出sql的逻辑

    private final ExprSqlTable sqlTable;//只对应一个逻辑表,有临时状态

    private final TableRouter tableRouter;



    public InsertExecution(LogicDbConfig logicDbConfig, SQLInsertStatement sqlStatement) throws Exception {
        this.logicDbConfig = logicDbConfig;
        this.originStatement = sqlStatement;
        if (originStatement.getQuery() != null) {
            throw new UnsupportedSqlClauseException("originStatement.getQuery() != null");
        }
        sqlTable = SqlTableParser.getSQLExprTable(originStatement.getTableSource(), logicDbConfig);
        if (sqlTable == null) {
            throw new RuntimeException("exprSqlTable == null");
        }
        SqlTableReferParser parser = new SqlTableReferParser(logicDbConfig, originStatement.getTableSource());
        sqlTable.setAlias(parser.getTableAlias());
        tableRouter = new DefaultTableRouter(logicDbConfig, sqlTable);
    }

    protected Map<Integer, SqlColumnValue> visitColumns(LogicTableConfig logicTableConfig, List<SQLExpr> columnExprs) throws SQLException {

        Map<Integer, SqlColumnValue> map = new HashMap<>();
        for (int i = 0; i < columnExprs.size(); i++) {
            SQLExpr sqlExpr = columnExprs.get(i);
            String columnName = SqlReferParser.getSQLIdentifier(sqlExpr);
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

    public final void addSqlLine(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {

        LogicTableConfig[] configPair = logicDbConfig.getLogicTableManager(sqlTable.getTableName()).getLogicTableConfig();
        LogicTableConfig logicTableConfig = configPair[0];
        //TODO 数据迁移时老区新区 周新区  有on duplacate key update时策略 新区老区双写，表格主键的获取
        //TODO no partition的处理 跳过
        //复制模式，修改其中的部分
        //key为physicTableName
        int lineNumber = logicSqlParameterHolder.getLineNumber();
        Map<Integer, SqlColumnValue> partitionColumnMap = visitColumns(logicTableConfig, originStatement.getColumns());
        RouteEvent routeEvent = new RouteEvent(originStatement, logicTableConfig, PartitionEvent.EventType.INSERT, logicSqlParameterHolder);
        routeEvent.setPartitionColumnMap(partitionColumnMap);
        routeEvent.setValuesClauseList(originStatement.getValuesList());
        Map<Partition, SqlTablePartitionSql> subsMap = tableRouter.route(routeEvent);
        for (Map.Entry<Partition, SqlTablePartitionSql> entry : subsMap.entrySet()) {
            SqlTablePartitionSql valuesClauses = entry.getValue();
            Partition sqlTablePartition = entry.getKey();
            String physicDbName = logicDbConfig.getPhysicDbConfig(sqlTablePartition.getPhysicDbName()).getPhysicDbName();
            PhysicTableExecutor sqlExecuteRouter = physicDbExecutor.get(physicDbName);
            PreparedPhysicSqlExecutor preparedSqlDbExecuteSql = sqlExecuteRouter.get(valuesClauses.getSql());
            if (preparedSqlDbExecuteSql == null) {
                preparedSqlDbExecuteSql = new PreparedPhysicSqlExecutor(valuesClauses.getSql(), physicDbName);
                sqlExecuteRouter.add(preparedSqlDbExecuteSql);
            }
            preparedSqlDbExecuteSql.addParameters(new LinedParameters(lineNumber, valuesClauses.getSqlParameters()));
        }
    }

    @Override
    public String toString() {
        return "InsertExecution{" + "sqlTable=" + sqlTable + '}';
    }
}
