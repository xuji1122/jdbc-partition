package org.the.force.jdbc.partition.engine.executor.factory;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.filter.QueryReferFilter;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.ParallelJoinedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.SubQueriedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.UnionQueriedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.WrappedSQLExprTableSource;
import org.the.force.jdbc.partition.engine.sqlelements.sqltable.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sqlelements.sqltable.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.engine.parser.table.SubQueryResetParser;
import org.the.force.jdbc.partition.engine.parser.table.TableConditionParser;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.the.force.thirdparty.druid.sql.parser.ParserException;

/**
 * Created by xuji on 2017/6/3.
 * 执行顺序 sqlTableSource --> 子查询 --> 自身（newWhere和聚合条件等）
 * 如果sqlTableSource是单表 则 子查询 --> 自身（tableSource newWhere和聚合条件等）
 * 如果没有子查询 自身（tableSource newWhere和聚合条件等）
 */
public class BlockQueryExecutorFactory implements QueryExecutorFactory {


    private QueryExecutor queryExecutor;


    public BlockQueryExecutorFactory(LogicDbConfig logicDbConfig, SQLSelectQueryBlock selectQuery) {
        this(logicDbConfig, selectQuery, null);
    }

    /**
     * @param logicDbConfig
     * @param selectQuery
     * @param queryReferFilter 这个参数是对selectQuery的查询结果集的过滤 表示的是SQLSelectQueryBlock来自于一个子查询
     */
    public BlockQueryExecutorFactory(LogicDbConfig logicDbConfig, SQLSelectQueryBlock selectQuery, QueryReferFilter queryReferFilter) {
        SQLTableSource from = selectQuery.getFrom();
        if (from instanceof SQLExprTableSource) {
            SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) from;
            ExprConditionalSqlTable sqlTable = new ExprConditionalSqlTable(logicDbConfig, sqlExprTableSource);
            TableConditionParser tableConditionParser = new TableConditionParser(logicDbConfig, sqlTable, selectQuery.getWhere());
            WrappedSQLExprTableSource wrappedSQLExprTableSource = new WrappedSQLExprTableSource(sqlTable);
            selectQuery.setFrom(wrappedSQLExprTableSource);
            //先做掉子查询，然后转为数据库的sql语句到数据库执行sql
            SQLExpr newWhere = tableConditionParser.getSubQueryResetWhere();
            selectQuery.setWhere(newWhere);
            //确保sqlTable被正确设置
            //group by 涉及的排序问题  没有group by 但是列有聚合查询也包括在内
            //order by 涉及的问题
            //limit涉及的问题
        } else {
            //tableSource先执行，再根据tableSource的结果生成查询结果集
            if (from instanceof SQLJoinTableSource) {
                //tableSource先执行，再根据tableSource的结果生成查询结果集
                ParallelJoinedTableSource parallelJoinedTableSource = new ParallelJoinedTableSource(logicDbConfig, (SQLJoinTableSource) from, selectQuery.getWhere());
                SQLExpr newWhere = parallelJoinedTableSource.getOtherCondition(); //tableSource特有的条件过滤掉之后剩余的条件
                //剩余的where条件是否有子查询
                if (newWhere != null) {
                    newWhere = (SQLExpr) new SubQueryResetParser(logicDbConfig, newWhere).getSubQueryResetSqlObject();
                }
                selectQuery.setFrom(parallelJoinedTableSource);
                selectQuery.setWhere(newWhere);
                //
            } else {
                //子查询的场景  from 是子查询
                ConditionalSqlTable sqlTable = new SqlTableParser(logicDbConfig).getSqlTable(from);
                TableConditionParser parser = new TableConditionParser(logicDbConfig, sqlTable, selectQuery.getWhere());
                SQLExpr newWhere = parser.getOtherCondition();
                if (newWhere != null) {
                    newWhere = (SQLExpr) new SubQueryResetParser(logicDbConfig, newWhere).getSubQueryResetSqlObject();
                }
                selectQuery.setWhere(newWhere);
                //确保sqlTable的alias被正确设置
                if (from instanceof SQLSubqueryTableSource) {
                    SubQueriedTableSource sqlTableSource = new SubQueriedTableSource(logicDbConfig, new QueryReferFilter(logicDbConfig, sqlTable));
                    selectQuery.setFrom(sqlTableSource);
                    //确保sqlTable被正确设置
                } else if (from instanceof SQLUnionQueryTableSource) {
                    UnionQueriedTableSource sqlTableSource = new UnionQueriedTableSource(logicDbConfig, new QueryReferFilter(logicDbConfig, sqlTable));
                    selectQuery.setFrom(sqlTableSource);
                    //确保sqlTable被正确设置
                } else {
                    //TODO
                    throw new ParserException("无法识别的tableSource:" + PartitionSqlUtils.toSql(selectQuery, logicDbConfig.getSqlDialect()) + " : from=" + from.getClass().getName());
                }
            }
        }
    }

    public QueryExecutor getQueryExecutor() {
        return queryExecutor;
    }
}
