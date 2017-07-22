package org.the.force.jdbc.partition.engine.executor.dql.partition;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.evaluator.factory.SqlExprEvaluatorFactory;
import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.dql.BlockQueryExecutor;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.engine.router.DefaultTableRouter;
import org.the.force.jdbc.partition.engine.router.TableRouter;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.SqlTableCitedLabels;
import org.the.force.jdbc.partition.engine.sql.query.SelectTable;
import org.the.force.jdbc.partition.engine.sql.table.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.engine.value.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/18.
 * 交给db执行的节点，需要路由和merge分区结果
 * * 为了让客户端能够merge结果集
 * 1，group by的列都必须放入select的结果集中，如果没有则改写sql放入
 *    如果aggregation 包括avg,那么对同样的参数 结果集中必须有count和sum，如果没有这改写sql放入
 * 2，having的条件涉及的aggregation必须放入select的结果集中
 * 3，order by的条件涉及的列也必须在select的结果集中
 * 4，如果group by和order by 同时存在，那么先依据group by的列排序，再依据order by重新排序
 */
public class PartitionBlockQueryExecutor extends SQLSelectQueryBlock implements BlockQueryExecutor {

    private final LogicDbConfig logicDbConfig;

    private final ExprConditionalSqlTable innerExprSqlTable;

    private final ConditionalSqlTable outerSqlTable;

    private final SQLSelectQueryBlock sqlSelectQueryBlock;

    private final TableRouter tableRouter;

    public PartitionBlockQueryExecutor(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock, ExprConditionalSqlTable innerExprSqlTable) {
        this.logicDbConfig = logicDbConfig;
        this.sqlSelectQueryBlock = sqlSelectQueryBlock;
        //最底层的sqlTable，和sqlSelectQueryBlock的tableSource未必是直接对应的
        this.innerExprSqlTable = innerExprSqlTable;
        tableRouter = new DefaultTableRouter(logicDbConfig, sqlSelectQueryBlock, innerExprSqlTable);
        outerSqlTable = new SqlTableParser(logicDbConfig).getSqlTable(sqlSelectQueryBlock.getFrom());

        SqlTableReferParser sqlTableReferParser = new SqlTableReferParser(logicDbConfig, sqlSelectQueryBlock, outerSqlTable);
        SqlTableCitedLabels sqlTableCitedLabels = sqlTableReferParser.getSqlTableCitedLabels();

        SqlExprEvaluatorFactory sqlExprEvaluatorFactory = logicDbConfig.getSqlExprEvaluatorFactory();
        List<SQLSelectItem> sqlSelectItems = sqlSelectQueryBlock.getSelectList();
        SelectTable selectTable = null;
        if (sqlSelectQueryBlock.getDistionOption() > 0) {
            selectTable = new SelectTable(outerSqlTable, true);
        } else {
            selectTable = new SelectTable(outerSqlTable, false);
        }
        int size = sqlSelectItems.size();
        int columnIndex = 0;
        List<SQLSelectItem> newItems = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SQLSelectItem oldItem = sqlSelectItems.get(i);
            SQLExpr itemExpr = oldItem.getExpr();
            if (itemExpr instanceof SQLAllColumnExpr) {
                if (oldItem.getAlias() != null) {
                    throw new SqlParseException("oldItem.getAlias()!=null and SQLAllColumnExpr");
                }

            } else if (itemExpr instanceof SQLPropertyExpr) {
            } else if (itemExpr instanceof SQLName) {

            } else {

            }
        }
        //group by
        //order by
        //limit
    }

    public void setLimit(SQLLimit limit) {
        //响应 limit的改变做出改变
        sqlSelectQueryBlock.setLimit(limit);
    }

    protected void accept0(SQLASTVisitor visitor) {
        sqlSelectQueryBlock.accept(visitor);
    }

    public SQLSelectQuery getStatement() {
        return sqlSelectQueryBlock;
    }

    public ResultSet execute(QueryCommand queryCommand, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {

        return null;
    }

    public SQLExpr getExpr() {
        throw new UnsupportedOperationException("getExpr()");
    }

    public void setExpr(SQLExpr expr) {

    }

    public String getAlias() {
        throw new UnsupportedOperationException("getAlias()");
    }

    public void setAlias(String alias) {

    }

    public int getHintsSize() {
        throw new UnsupportedOperationException("getHintsSize()");
    }

    public List<SQLHint> getHints() {
        throw new UnsupportedOperationException("getHints()");
    }

    public void setHints(List<SQLHint> hints) {

    }

    public SQLExprTableSource clone() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public String computeAlias() {
        throw new UnsupportedOperationException("computeAlias()");
    }

    public SQLExpr getFlashback() {
        throw new UnsupportedOperationException("getFlashback()");
    }

    public void setFlashback(SQLExpr flashback) {
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public String toString() {
        return PartitionSqlUtils.toSql(this, logicDbConfig.getSqlDialect());
    }

    public ExprConditionalSqlTable getInnerExprSqlTable() {
        return innerExprSqlTable;
    }

    public ConditionalSqlTable getOuterSqlTable() {
        return outerSqlTable;
    }
}
