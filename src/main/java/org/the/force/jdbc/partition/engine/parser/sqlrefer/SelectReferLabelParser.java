package org.the.force.jdbc.partition.engine.parser.sqlrefer;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.parser.elements.ExprSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.exception.UnsupportedSqlOperatorException;
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

import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xuji on 2017/6/14.
 * 解析select查询结果集中可以被引用的列
 */
public class SelectReferLabelParser {

    private final LogicDbConfig logicDbConfig;

    public SelectReferLabelParser(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    public Set<String> parseSelectLabels(SQLUnionQuery sqlUnionQuery) throws SQLException {
        SQLSelectQuery rightSqlSelectQuery = sqlUnionQuery.getRight();
        Set<String> ls = parseSelectLabels(rightSqlSelectQuery);
        if (ls == null || ls.isEmpty()) {
            return parseSelectLabels(sqlUnionQuery.getLeft());
        }
        return new LinkedHashSet<>();
    }

    public Set<String> parseSelectLabels(SQLSelectQuery sqlSelectQuery) throws SQLException {
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            return parseSelectLabels((SQLSelectQueryBlock) sqlSelectQuery);
        } else if (sqlSelectQuery instanceof SQLUnionQuery) {
            return parseSelectLabels((SQLUnionQuery) sqlSelectQuery);
        } else {
            throw new UnsupportedSqlOperatorException(
                "sqlSelectQuery not block and not union" + PartitionSqlUtils.toSql(sqlSelectQuery, logicDbConfig.getSqlDialect()) + "\n" + sqlSelectQuery.getClass());
        }
    }

    /**
     * @param sqlSelectQueryBlock
     * @return
     */
    public Set<String> parseSelectLabels(SQLSelectQueryBlock sqlSelectQueryBlock) throws SQLException {
        List<SQLSelectItem> selectList = sqlSelectQueryBlock.getSelectList();
        Set<String> columns = new LinkedHashSet<>();
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
                SqlRefer sqlRefer = SqlReferParser.getSqlRefer(sqlPropertyExpr);
                if (sqlRefer == null) {
                    throw new SqlParseException("无法识别select的item的label");
                }
                Set<String> set = getAllColumns(sqlSelectQueryBlock.getFrom(), sqlRefer.getOwnerName());
                columns.addAll(set);
            } else if (expr instanceof SQLAllColumnExpr) {
                Set<String> set = getAllColumns(sqlSelectQueryBlock.getFrom(), null);
                columns.addAll(set);
            } else if (expr instanceof SQLName) {
                SQLName sqlName = (SQLName) expr;
                String name = sqlName.getSimpleName();
                columns.add(name);
            } else {
                throw new SqlParseException("无法识别select的item的label");
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
    public Set<String> getAllColumns(SQLTableSource sqlTableSource, String targetTableName) throws SQLException {
        if (sqlTableSource instanceof SQLExprTableSource) {
            SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) sqlTableSource;
            ExprSqlTable exprSqlTable = SqlTableParser.getSQLExprTable(sqlExprTableSource, logicDbConfig);
            if (targetTableName == null || targetTableName.equals(exprSqlTable.getAlias()) || targetTableName.equalsIgnoreCase(exprSqlTable.getTableName())) {
                return exprSqlTable.getReferLabels();
            } else {
                return new LinkedHashSet<>();
            }
        } else if (sqlTableSource instanceof SQLJoinTableSource) {
            //从多个中选择一个，必须指定targetTableName
            SQLJoinTableSource joinTableSource = (SQLJoinTableSource) sqlTableSource;

            Set<String> ls = getAllColumns(joinTableSource.getLeft(), targetTableName);
            if (targetTableName != null && !ls.isEmpty()) {
                return ls;
            }
            /**
             * 1，获取所有tableSource的所有列时继续取right的table的列
             * 2，targetTableName不为空但是left的tableName没有匹配上
             */
            Set<String> rs = getAllColumns(joinTableSource.getRight(), targetTableName);
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
                throw new UnsupportedSqlOperatorException("sqlSelectQuery == null");
            }
            Set<String> sets = parseSelectLabels(sqlSelectQuery);
            if (targetTableName == null || targetTableName.equals(sqlTableSource.getAlias())) {
                return sets;
            } else {
                return new LinkedHashSet<>();
            }

        } else if (sqlTableSource instanceof SQLUnionQueryTableSource) {
            SQLUnionQueryTableSource unionQueryTableSource = (SQLUnionQueryTableSource) sqlTableSource;
            Set<String> sets = parseSelectLabels(unionQueryTableSource.getUnion());
            if (targetTableName == null || targetTableName.equals(sqlTableSource.getAlias())) {
                return sets;
            } else {
                return new LinkedHashSet<>();
            }
        } else {
            throw new SqlParseException("无法识别的tableSource类型" + sqlTableSource.getClass().getName());
        }
    }

}
