package org.the.force.jdbc.partition.engine.executor.dql.executor;

import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.elements.table.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.parameter.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSourceImpl;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/18.
 * 确保sqlTable被正确设置
 * group by 涉及的排序问题  没有group by 但是列有聚合查询也包括在内
 * order by 涉及的问题
 * limit涉及的问题
 */
public class DbBlockQueryExecutor extends SQLTableSourceImpl implements BlockQueryExecutor {

    private final LogicDbConfig logicDbConfig;

    private final ExprConditionalSqlTable sqlTable;

    private final SQLSelectQueryBlock sqlSelectQueryBlock;


    public DbBlockQueryExecutor(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock, ExprConditionalSqlTable sqlTable) {
        this.logicDbConfig = logicDbConfig;
        this.sqlSelectQueryBlock = sqlSelectQueryBlock;
        //最底层的sqlTable，和sqlSelectQueryBlock的tableSource未必是直接对应的
        this.sqlTable = sqlTable;
    }

    protected void accept0(SQLASTVisitor visitor) {
        sqlTable.getSQLTableSource().accept(visitor);
    }

    public ResultSet execute(QueryCommand queryCommand, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
        return null;
    }

    public String getAlias() {
        return sqlTable.getSQLTableSource().getAlias();
    }

    public void setAlias(String alias) {

    }

    public int getHintsSize() {
        return sqlTable.getSQLTableSource().getHints().size();
    }

    public List<SQLHint> getHints() {
        return sqlTable.getSQLTableSource().getHints();
    }

    public void setHints(List<SQLHint> hints) {

    }

    public SQLTableSource clone() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public String computeAlias() {
        return alias;
    }

    public SQLExpr getFlashback() {
        return sqlTable.getSQLTableSource().getFlashback();
    }

    public void setFlashback(SQLExpr flashback) {
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public ConditionalSqlTable getSqlTable() {
        return sqlTable;
    }

    public SQLSelectQueryBlock getSqlSelectQueryBlock() {
        return sqlSelectQueryBlock;
    }
}
