package org.the.force.jdbc.partition.engine.executor.factory;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.executor.dql.blockquery.BlockQueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.blockquery.DbBlockQueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.blockquery.JoinedBlockQueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.ParallelJoinedTableSource;
import org.the.force.jdbc.partition.engine.parser.table.SubQueryResetParser;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;

/**
 * Created by xuji on 2017/6/3.
 * 执行顺序 sqlTableSource --> 子查询 --> 自身（newWhere和聚合条件等）
 * 如果sqlTableSource是单表 则 子查询 --> 自身（tableSource newWhere和聚合条件等）
 * 如果没有子查询 自身（tableSource newWhere和聚合条件等）
 */
public class BlockQueryExecutorFactory implements QueryExecutorFactory {

    private final LogicDbConfig logicDbConfig;

    private final SQLSelectQueryBlock selectQueryBlock;


    /**
     * @param logicDbConfig
     * @param selectQueryBlock
     */
    public BlockQueryExecutorFactory(LogicDbConfig logicDbConfig, SQLSelectQueryBlock selectQueryBlock) {
        if (selectQueryBlock.getFrom() instanceof SQLUnionQueryTableSource) {
            throw new SqlParseException("not supported SQLUnionQueryTableSource");
        }
        this.logicDbConfig = logicDbConfig;
        this.selectQueryBlock = selectQueryBlock;
    }

    public BlockQueryExecutor build() {
        return build(this.selectQueryBlock);
    }

    public BlockQueryExecutor build(SQLSelectQueryBlock selectQueryBlock) {
        //备份原始的
        SQLSelectQueryBlock original = selectQueryBlock;
        do {
            if (selectQueryBlock.getFrom() instanceof SQLExprTableSource) {
                return new DbBlockQueryExecutor(original);
            } else if (selectQueryBlock.getFrom() instanceof SQLJoinTableSource) {
                resetJoinTableSource(selectQueryBlock);
                return new JoinedBlockQueryExecutor(original);
            } else if (!(selectQueryBlock.getFrom() instanceof SQLSubqueryTableSource)) {
                //TODO
                throw new SqlParseException(
                    "不支持的tableSource:" + PartitionSqlUtils.toSql(selectQueryBlock, logicDbConfig.getSqlDialect()) + " : from=" + selectQueryBlock.getFrom().getClass().getName());
            }

            selectQueryBlock = checkSQLSubqueryTableSource(selectQueryBlock);

        } while (true);
    }

    private SQLSelectQueryBlock checkSQLSubqueryTableSource(SQLSelectQueryBlock selectQueryBlock) {
        SQLSubqueryTableSource subqueryTableSource = (SQLSubqueryTableSource) selectQueryBlock.getFrom();
        SQLSelectQuery sqlSelectQuery = subqueryTableSource.getSelect().getQuery();
        if (sqlSelectQuery == null) {
            throw new SqlParseException("query sqlSelectQuery == null");
        }
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock) sqlSelectQuery;
        } else {
            throw new SqlParseException("unsupported select query:" + PartitionSqlUtils.toSql(sqlSelectQuery, logicDbConfig.getSqlDialect()));
        }
    }

    public void resetJoinTableSource(SQLSelectQueryBlock selectQueryBlock) {
        ParallelJoinedTableSource parallelJoinedTableSource =
            new ParallelJoinedTableSource(logicDbConfig, (SQLJoinTableSource) selectQueryBlock.getFrom(), selectQueryBlock.getWhere());
        SQLExpr newWhere = parallelJoinedTableSource.getOtherCondition(); //tableSource特有的条件过滤掉之后剩余的条件
        //剩余的where条件是否有子查询
        if (newWhere != null) {
            newWhere = (SQLExpr) new SubQueryResetParser(logicDbConfig, newWhere).getSubQueryResetSqlObject();
        }
        selectQueryBlock.setFrom(parallelJoinedTableSource);
        selectQueryBlock.setWhere(newWhere);
    }


}
