package org.the.force.jdbc.partition.engine.executor.dql.logic;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.dql.BlockQueryExecutor;
import org.the.force.jdbc.partition.engine.value.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.SQLLimit;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/18.
 * jdbc端实现的sql
 * 准确的定义是 from的结果集可以获取到，但是对from的过滤，聚合，排序等问题只能由jdbc自己实现，不能依赖数据库的过滤，聚合以及排序结果
 * 它的特点是
 * 理论上不受数据库执行的sql的约束，不必merge多个分区的结果，只要处理好一个结果集即可，但是会面临数据量大的问题
 * ==================适用范围===============
 * 1，from是join类型的 {@link JoinedTableBlockQuery}
 * 2，from是单表查询
 * 但是中间的from子查询中有limit 条件
 * 此时只能先做里面的子查询 再由jdbc端对from返回的数据进行过滤，聚合，排序等操作 {@link PassiveBlockQuery}
 * 3, 1或2的情况作子查询时，外部的查询被迫只能由jdbc自己实现{@link PassiveBlockQuery}
 */
public abstract class LogicBlockQueryExecutor extends SQLSelectQueryBlock implements BlockQueryExecutor {

    protected final LogicDbConfig logicDbConfig;

    protected final SQLSelectQueryBlock sqlSelectQueryBlock;

    public LogicBlockQueryExecutor(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock) {
        this.logicDbConfig = logicDbConfig;
        this.sqlSelectQueryBlock = sqlSelectQueryBlock;
    }



    public SQLSelectQuery getStatement() {
        return sqlSelectQueryBlock;
    }

    public ResultSet execute(QueryCommand queryCommand, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
        return null;
    }

    public void init() {

    }

    public void setLimit(SQLLimit limit) {
        sqlSelectQueryBlock.setLimit(limit);
    }

    public SQLExpr getExpr() {
        return null;
    }

    public void setExpr(SQLExpr expr) {
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

}
