package org.the.force.jdbc.partition.engine.executor.dql.logic;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.dql.BlockQueryExecutor;
import org.the.force.jdbc.partition.engine.parser.copy.SqlObjCopier;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.value.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelect;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/18.
 * client端实现的sql
 * 准确的定义是 from的结果集可以获取到，但是对from的过滤，聚合，排序等问题只能由client自己实现，不能依赖数据库的过滤，聚合以及排序结果
 * 它的特点是
 * 理论上不受数据库执行的sql的约束，不必merge多个分区的结果，只要处理好一个结果集即可，但是会面临数据量大的问题
 * ==================适用范围===============
 * 1，from是join类型的
 * 2，from是单表查询{@link org.the.force.jdbc.partition.engine.executor.dql.partition.PartitionBlockQueryExecutor}
 *    但是中间的from子查询中有limit 条件 只能先做里面的子查询 再由client对from返回的数据进行过滤，聚合，排序等操作
 * 3, 1或2的情况作子查询时，外部的查询被迫只能由client自己实现
 */
public class LogicBlockQueryExecutor extends SQLSelectQueryBlock implements BlockQueryExecutor {

    private final LogicDbConfig logicDbConfig;

    private final SQLSelectQueryBlock sqlSelectQueryBlock;

    private final ConditionalSqlTable originalConditionalSqlTable;

    public LogicBlockQueryExecutor(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock, ConditionalSqlTable originalConditionalSqlTable) {
        this.logicDbConfig = logicDbConfig;
        this.sqlSelectQueryBlock = sqlSelectQueryBlock;
        this.originalConditionalSqlTable = originalConditionalSqlTable;
    }

    protected void accept0(SQLASTVisitor visitor) {
        //只在打印调试的时候使用
        SQLSelectQueryBlock sqlSelectQueryBlock = this.sqlSelectQueryBlock;
        SQLTableSource sqlTableSource = sqlSelectQueryBlock.getFrom();
        if ((sqlTableSource instanceof BlockQueryExecutor) && originalConditionalSqlTable != null) {
            BlockQueryExecutor blockQueryExecutor = (BlockQueryExecutor) sqlTableSource;
            SQLSubqueryTableSource subqueryTableSource = new SQLSubqueryTableSource(new SQLSelect(blockQueryExecutor), originalConditionalSqlTable.getAlias());
            SqlObjCopier sqlObjCopier = new SqlObjCopier();
            sqlObjCopier.addReplaceObj(sqlSelectQueryBlock.getFrom(), subqueryTableSource);
            sqlSelectQueryBlock = sqlObjCopier.copy(sqlSelectQueryBlock);
            sqlSelectQueryBlock.accept(visitor);
        } else {
            sqlSelectQueryBlock.accept(visitor);
        }
    }


    public SQLSelectQuery getStatement() {
        return sqlSelectQueryBlock;
    }

    public ResultSet execute(QueryCommand queryCommand, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
        return null;
    }

    public void setLimit(SQLLimit limit) {
        //响应 limit的改变做出改变
        sqlSelectQueryBlock.setLimit(limit);
    }

    public SQLExpr getExpr() {
        return null;
    }

    public void setExpr(SQLExpr expr) {
    }

    public String getAlias() {
        if (originalConditionalSqlTable != null) {
            return originalConditionalSqlTable.getSQLTableSource().getAlias();
        } else {
            throw new UnsupportedOperationException("getAlias()");
        }
    }

    public void setAlias(String alias) {

    }

    public int getHintsSize() {
        throw new UnsupportedOperationException("getAlias()");
    }

    public List<SQLHint> getHints() {
        throw new UnsupportedOperationException("getHints()");
    }

    public void setHints(List<SQLHint> hints) {

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


    public SQLSelectQueryBlock getSqlSelectQueryBlock() {
        return sqlSelectQueryBlock;
    }


    public String toString() {
        return PartitionSqlUtils.toSql(this, logicDbConfig.getSqlDialect());
    }

    public SQLTableSource getOriginalSqlTableSource() {
        if (originalConditionalSqlTable != null) {
            return originalConditionalSqlTable.getSQLTableSource();
        } else {
            return null;
        }
    }
}
