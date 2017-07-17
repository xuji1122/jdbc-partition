package org.the.force.jdbc.partition.engine.executor.dql.select;

import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.filter.QueryReferFilter;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.WrappedSQLExprTableSource;
import org.the.force.jdbc.partition.engine.parameter.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.router.TableRouter;
import org.the.force.jdbc.partition.engine.sqlelements.sqltable.ConditionalSqlTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/12.
 * 解决聚合查询在线动态扩容的场景下
 * 选择TableParitionGroupByQuery或TablePartitionUnionQuery是动态的
 * 此时通过TableParitionQueryAdapter动态实现TablePartitionQuery
 */
public class PartitionRowQuery implements QueryExecutor {

    private final LogicDbConfig logicDbConfig;

    //condition在inputQueryBlock中

    private final SQLSelectQueryBlock inputQueryBlock;

    private final ConditionalSqlTable sqlTable;

    private final TableRouter tableRouter;

    private QueryReferFilter queryReferFilter;


    public PartitionRowQuery(LogicDbConfig logicDbConfig, SQLSelectQueryBlock inputQueryBlock) {
        this.logicDbConfig = logicDbConfig;
        this.inputQueryBlock = inputQueryBlock;
        WrappedSQLExprTableSource sqlExprTableSource = (WrappedSQLExprTableSource) inputQueryBlock.getFrom();
        sqlTable = sqlExprTableSource.getSqlTable();
        tableRouter = null;
        //parse group by type

        //parse limit  实际上什么也不用做
    }

    public ResultSet execute(QueryCommand queryCommand, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
        return null;
    }

    public QueryReferFilter getQueryReferFilter() {
        return queryReferFilter;
    }

    public void setQueryReferFilter(QueryReferFilter queryReferFilter) {
        this.queryReferFilter = queryReferFilter;
    }
}
