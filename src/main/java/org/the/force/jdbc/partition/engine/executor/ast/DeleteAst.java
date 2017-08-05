package org.the.force.jdbc.partition.engine.executor.ast;

import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.table.TableConditionParser;
import org.the.force.jdbc.partition.engine.router.DefaultTableRouter;
import org.the.force.jdbc.partition.engine.router.RouteEvent;
import org.the.force.jdbc.partition.engine.router.TableRouter;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.stmt.SqlTablePartition;
import org.the.force.jdbc.partition.engine.stmt.table.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLDeleteStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by xuji on 2017/5/18.
 */
public class DeleteAst extends AbstractExecutableAst {

    private static Log logger = LogFactory.getLog(DeleteAst.class);

    private final SQLDeleteStatement sqlDeleteStatement;

    protected final ExprConditionalSqlTable exprSqlTable;

    private final TableRouter tableRouter;

    public DeleteAst(LogicDbConfig logicDbConfig, SQLDeleteStatement sqlStatement) throws Exception {
        super(logicDbConfig);
        this.sqlDeleteStatement = sqlStatement;
        exprSqlTable = new ExprConditionalSqlTable(logicDbConfig, (SQLExprTableSource) sqlDeleteStatement.getTableSource());
        new TableConditionParser(logicDbConfig, exprSqlTable, sqlDeleteStatement.getWhere());
        new SqlTableReferParser(logicDbConfig, sqlDeleteStatement, exprSqlTable);
        tableRouter = new DefaultTableRouter(logicDbConfig, exprSqlTable);
    }

    public Map<Partition, SqlTablePartition> doRoute(SqlLineExecRequest sqlLineExecRequest) throws SQLException {
        LogicTableConfig[] configPair = logicDbConfig.getLogicTableManager(exprSqlTable.getTableName()).getLogicTableConfig();
        LogicTableConfig logicTableConfig = configPair[0];
        //TODO 数据迁移时老区新区 周新区 update时策略 新区老区双写，表格主键的获取
        RouteEvent routeEvent = new RouteEvent(logicTableConfig, PartitionEvent.EventType.UPDATE, sqlLineExecRequest);
        return tableRouter.route(routeEvent);
    }

    public SQLStatement getOriginStatement() {
        return sqlDeleteStatement;
    }

    public String toString() {
        return "DeleteAst{" + "sqlDeleteStatement=" + sqlDeleteStatement + ", exprSqlTable=" + exprSqlTable + ", tableRouter=" + tableRouter + '}';
    }
}
