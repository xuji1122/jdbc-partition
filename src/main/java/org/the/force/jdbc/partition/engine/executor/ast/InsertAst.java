package org.the.force.jdbc.partition.engine.executor.ast;

import org.the.force.jdbc.partition.engine.evaluator.factory.SqlExprEvaluatorFactory;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.router.InsertTableRouter;
import org.the.force.jdbc.partition.engine.router.RouteEvent;
import org.the.force.jdbc.partition.engine.router.TableRouter;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.stmt.SqlRefer;
import org.the.force.jdbc.partition.engine.stmt.SqlTablePartition;
import org.the.force.jdbc.partition.engine.stmt.table.InsertSqlTable;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.exception.UnsupportedSqlClauseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionEvent;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/5/18.
 * insert 一行
 * insert 多行
 * insert时生成主键
 */
public class InsertAst extends AbstractExecutableAst {

    private final SQLInsertStatement originStatement;//输出物理sql的模板，本身不会变，但是outputVisitor会重写输出sql的逻辑

    private final InsertSqlTable sqlTable;//只对应一个逻辑表,有临时状态

    private final TableRouter tableRouter;


    public InsertAst(LogicDbConfig logicDbConfig, SQLInsertStatement sqlStatement) throws Exception {
        super(logicDbConfig);
        this.originStatement = sqlStatement;
        if (originStatement.getQuery() != null) {
            throw new UnsupportedSqlClauseException("originStatement.getQuery() != null");
        }
        //TODO 添加自增长列
        sqlTable = new InsertSqlTable(logicDbConfig, originStatement.getTableSource());
        new SqlTableReferParser(logicDbConfig, originStatement, sqlTable);
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

    public Map<Partition, SqlTablePartition> doRoute(SqlLineExecRequest sqlLineExecRequest) throws SQLException {
        LogicTableConfig[] configPair = logicDbConfig.getLogicTableManager(sqlTable.getTableName()).getLogicTableConfig();
        LogicTableConfig logicTableConfig = configPair[0];
        //TODO 数据迁移时老区新区 周新区  有on duplacate key update时策略 新区老区双写，表格主键的获取
        //TODO no partition的处理 跳过
        RouteEvent routeEvent = new RouteEvent(logicTableConfig, PartitionEvent.EventType.INSERT, sqlLineExecRequest);
        return tableRouter.route(routeEvent);
    }

    public SQLStatement getOriginStatement() {
        return originStatement;
    }

    public String toString() {
        return "InsertAst{" + "sqlTable=" + sqlTable + '}';
    }
}
