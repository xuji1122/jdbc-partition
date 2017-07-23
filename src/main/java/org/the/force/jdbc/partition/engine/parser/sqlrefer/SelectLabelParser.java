package org.the.force.jdbc.partition.engine.parser.sqlrefer;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.JoinedTableSource;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.SqlRefer;
import org.the.force.jdbc.partition.engine.sql.table.ExprConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.table.ExprSqlTable;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/6/14.
 * 解析select查询结果集中可以被引用的列
 * 只操作原始的SQLTableSource
 */
public class SelectLabelParser {

    private static Log logger = LogFactory.getLog(SelectLabelParser.class);

    private final LogicDbConfig logicDbConfig;

    public SelectLabelParser(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    public List<String> parseSelectLabels(SQLUnionQuery sqlUnionQuery)  {
        SQLSelectQuery rightSqlSelectQuery = sqlUnionQuery.getRight();
        List<String> ls = parseSelectLabels(rightSqlSelectQuery);
        if (ls == null || ls.isEmpty()) {
            return parseSelectLabels(sqlUnionQuery.getLeft());
        }
        return new ArrayList<>();
    }

    public List<String> parseSelectLabels(SQLSelectQuery sqlSelectQuery) {
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            return parseSelectLabels((SQLSelectQueryBlock) sqlSelectQuery);
        } else if (sqlSelectQuery instanceof SQLUnionQuery) {
            return parseSelectLabels((SQLUnionQuery) sqlSelectQuery);
        } else {
            throw new UnsupportedOperationException(
                "sqlSelectQuery not block and not union" + PartitionSqlUtils.toSql(sqlSelectQuery, logicDbConfig.getSqlDialect()) + "\n" + sqlSelectQuery.getClass());
        }
    }

    /**
     * @param sqlSelectQueryBlock
     * @return
     */
    public List<String> parseSelectLabels(SQLSelectQueryBlock sqlSelectQueryBlock)  {
        List<SQLSelectItem> selectList = sqlSelectQueryBlock.getSelectList();
        List<String> columns = new ArrayList<>();
        for (SQLSelectItem item : selectList) {
            if (item.getAlias() != null) {
                //大小写敏感
                columns.add(item.getAlias());
                continue;
            }
            SQLExpr expr = item.getExpr();
            if (expr instanceof SQLPropertyExpr) {
                //SQLPropertyExpr  t.*  name=*
                SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) expr;
                if (!sqlPropertyExpr.getName().equalsIgnoreCase("*")) {
                    columns.add(sqlPropertyExpr.getName());
                    continue;
                }
                SqlRefer sqlRefer = new SqlRefer(sqlPropertyExpr);
                List<String> set = getAllColumns(sqlSelectQueryBlock.getFrom(), sqlRefer.getOwnerName());
                columns.addAll(set);
            } else if (expr instanceof SQLAllColumnExpr) {
                List<String> set = getAllColumns(sqlSelectQueryBlock.getFrom(), null);
                columns.addAll(set);
            } else if (expr instanceof SQLName) {
                SQLName sqlName = (SQLName) expr;
                String name = sqlName.getSimpleName();
                columns.add(name);
            } else {
                //无法被引用，以特殊字符串标识
                columns.add("'" + expr.toString() + "'");
            }
        }
        return columns;
    }

    /**
     * @param sqlTableSource
     * @param targetTableName 查找的目标表名  为null时代表查询所有的tableSource
     * @return
     * @throws SQLException
     */
    public List<String> getAllColumns(SQLTableSource sqlTableSource, String targetTableName)  {
        if (sqlTableSource instanceof SQLExprTableSource) {
            SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) sqlTableSource;
            ExprSqlTable exprSqlTable = new ExprConditionalSqlTable(logicDbConfig, sqlExprTableSource);
            if (targetTableName == null || targetTableName.equals(exprSqlTable.getAlias()) || targetTableName.equalsIgnoreCase(exprSqlTable.getTableName())) {
                return exprSqlTable.getAllReferAbleLabels();
            } else {
                return new ArrayList<>();
            }
        } else if (sqlTableSource instanceof JoinedTableSource) {
            //TODO 获取所有label
            JoinedTableSource joinedTableSource = (JoinedTableSource) sqlTableSource;
            List<ConditionalSqlTable> sqlTables = joinedTableSource.getSqlTables();
            List<QueryExecutor> executorList = joinedTableSource.getQueryExecutors();
            List<String> returnList = new ArrayList<>();
            for (int i = 0; i < sqlTables.size(); i++) {
                ConditionalSqlTable conditionalSqlTable = sqlTables.get(i);
                if (targetTableName != null) {
                    if (targetTableName.equals(conditionalSqlTable.getAlias()) || targetTableName.equalsIgnoreCase(conditionalSqlTable.getTableName())) {
                        return parseSelectLabels(executorList.get(i).getStatement());
                    }
                } else {
                    List<String> temp = parseSelectLabels(executorList.get(i).getStatement());
                    returnList.addAll(temp);
                }
            }
            return returnList;
        } else if (sqlTableSource instanceof SQLJoinTableSource) {
            //从多个中选择一个，必须指定targetTableName
            SQLJoinTableSource joinTableSource = (SQLJoinTableSource) sqlTableSource;

            List<String> ls = getAllColumns(joinTableSource.getLeft(), targetTableName);
            if (targetTableName != null && !ls.isEmpty()) {
                return ls;
            }
            /**
             * 1，获取所有tableSource的所有列时继续取right的table的列
             * 2，targetTableName不为空但是left的tableName没有匹配上
             */
            List<String> rs = getAllColumns(joinTableSource.getRight(), targetTableName);
            if (targetTableName == null) {
                ls.addAll(rs);
                return ls;
            }
            return rs;
        }
        if (sqlTableSource.getAlias() == null) {
            throw new SqlParseException("sqlTableSource.getAlias() == null");
        }
        if (sqlTableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource subqueryTableSource = (SQLSubqueryTableSource) sqlTableSource;
            SQLSelectQuery sqlSelectQuery = subqueryTableSource.getSelect().getQuery();
            if (sqlSelectQuery == null) {
                throw new UnsupportedOperationException("sqlSelectQuery == null");
            }
            List<String> sets = parseSelectLabels(sqlSelectQuery);
            if (targetTableName == null || targetTableName.equals(sqlTableSource.getAlias())) {
                return sets;
            } else {
                return new ArrayList<>();
            }

        } else if (sqlTableSource instanceof SQLUnionQueryTableSource) {
            SQLUnionQueryTableSource unionQueryTableSource = (SQLUnionQueryTableSource) sqlTableSource;
            List<String> sets = parseSelectLabels(unionQueryTableSource.getUnion());
            if (targetTableName == null || targetTableName.equals(sqlTableSource.getAlias())) {
                return sets;
            } else {
                return new ArrayList<>();
            }
        } else {
            throw new SqlParseException("无法识别的tableSource类型" + sqlTableSource.getClass().getName());
        }
    }

}
