package org.the.force.jdbc.partition.engine.executor.query.select;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.query.QueryReferFilter;
import org.the.force.jdbc.partition.engine.executor.QueryExecution;
import org.the.force.jdbc.partition.engine.executor.query.SubQueryFilter;
import org.the.force.jdbc.partition.engine.parser.elements.ExprSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumn;
import org.the.force.jdbc.partition.engine.parser.router.DefaultTableRouter;
import org.the.force.jdbc.partition.engine.parser.router.TableRouter;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by xuji on 2017/7/12.
 * 解决聚合查询在线动态扩容的场景下
 * 选择TableParitionGroupByQuery或TablePartitionUnionQuery是动态的
 * 此时通过TableParitionQueryAdapter动态实现TablePartitionQuery
 */
public class PartitionRowQuery implements QueryExecution {

    private final LogicDbConfig logicDbConfig;

    //condition在inputQueryBlock中
    private final SQLSelectQueryBlock inputQueryBlock;

    private final ExprSqlTable sqlTable;

    private final Map<SqlColumn, SQLExpr> currentTableColumnValueMap;

    private final Map<SqlColumn, SQLInListExpr> currentTableColumnInValuesMap;

    private final TableRouter tableRouter;

    private SubQueryFilter subQueryFilter;

    private QueryReferFilter queryReferFilter;


    public PartitionRowQuery(LogicDbConfig logicDbConfig, SQLSelectQueryBlock inputQueryBlock,  Map<SqlColumn, SQLExpr> currentTableColumnValueMap,
        Map<SqlColumn, SQLInListExpr> currentTableColumnInValuesMap) {
        this.logicDbConfig = logicDbConfig;
        this.inputQueryBlock = inputQueryBlock;
        this.currentTableColumnValueMap = currentTableColumnValueMap;
        this.currentTableColumnInValuesMap = currentTableColumnInValuesMap;
        SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) inputQueryBlock.getFrom();
        sqlTable = (ExprSqlTable) new SqlTableParser(logicDbConfig).getSqlTable(sqlExprTableSource);
        //判断sqlTable是否需要强制alias
        new SqlTableReferParser(logicDbConfig, sqlExprTableSource, sqlTable);
        tableRouter = new DefaultTableRouter(logicDbConfig, sqlTable);

        //parse group by type

        //parse limit  实际上什么也不用做
    }

    public ResultSet execute(QueryCommand queryCommand, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
        return null;
    }


    public SubQueryFilter getSubQueryFilter() {
        return subQueryFilter;
    }

    public void setSubQueryFilter(SubQueryFilter subQueryFilter) {
        this.subQueryFilter = subQueryFilter;
    }

    public QueryReferFilter getQueryReferFilter() {
        return queryReferFilter;
    }

    public void setQueryReferFilter(QueryReferFilter queryReferFilter) {
        this.queryReferFilter = queryReferFilter;
    }
}
