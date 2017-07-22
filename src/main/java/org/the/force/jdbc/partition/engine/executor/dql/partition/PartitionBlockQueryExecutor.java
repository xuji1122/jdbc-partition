package org.the.force.jdbc.partition.engine.executor.dql.partition;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.dql.BlockQueryExecutor;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.table.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.engine.value.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/18.
 * 交给db执行的节点，需要路由和merge分区结果
 */
public class PartitionBlockQueryExecutor extends SQLExprTableSource implements BlockQueryExecutor {

    private final LogicDbConfig logicDbConfig;

    private final ExprConditionalSqlTable innerExprSqlTable;

    private final SQLSelectQueryBlock sqlSelectQueryBlock;


    public PartitionBlockQueryExecutor(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock, ExprConditionalSqlTable innerExprSqlTable) {
        this.logicDbConfig = logicDbConfig;
        this.sqlSelectQueryBlock = sqlSelectQueryBlock;
        //最底层的sqlTable，和sqlSelectQueryBlock的tableSource未必是直接对应的
        this.innerExprSqlTable = innerExprSqlTable;
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
        return alias;
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
        return PartitionSqlUtils.toSql(sqlSelectQueryBlock, logicDbConfig.getSqlDialect());
    }

    public ExprConditionalSqlTable getInnerExprSqlTable() {
        return innerExprSqlTable;
    }
}
