package org.the.force.jdbc.partition.engine.parser.sqlName;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.plan.model.SqlExprTable;
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
 */
public class SelectLabelParser {

    private final LogicDbConfig logicDbConfig;

    public SelectLabelParser(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    public Set<String> parseSelectLabels(SQLUnionQuery sqlUnionQuery) throws SQLException {
        SQLSelectQuery rightSqlSelectQuery = sqlUnionQuery.getRight();
        Set<String> ls = parseSelectLabels(rightSqlSelectQuery);
        if (ls == null) {
            return parseSelectLabels(sqlUnionQuery.getLeft());
        }
        return null;
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
     * TODO select *  select t.* 如何处理的问题
     *
     * @param sqlSelectQueryBlock
     * @return
     */
    public Set<String> parseSelectLabels(SQLSelectQueryBlock sqlSelectQueryBlock) throws SQLException {
        List<SQLSelectItem> selectList = sqlSelectQueryBlock.getSelectList();
        Set<String> columns = new LinkedHashSet<>();
        for (SQLSelectItem item : selectList) {
            if (item.getAlias() != null) {
                columns.add(item.getAlias());
            }
            SQLExpr expr = item.getExpr();
            if (expr instanceof SQLPropertyExpr) {
                //SQLPropertyExpr  t.*  name=*
                SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) expr;
                if (!sqlPropertyExpr.getName().equalsIgnoreCase("*")) {
                    columns.add(sqlPropertyExpr.getName());
                    continue;
                }
                SqlProperty sqlProperty = SqlNameParser.getSqlProperty(sqlPropertyExpr);
                if (sqlProperty == null) {
                    throw new SqlParseException("无法识别select的item的label");
                }
                sqlSelectQueryBlock.getFrom();
            } else if (expr instanceof SQLAllColumnExpr) {
                // TODO select * 如何处理的问题

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

    public Set<String> getAllColumns(SQLTableSource sqlTableSource, String targetTableName) throws SQLException {
        if (targetTableName == null) {
            throw new SqlParseException("targetTableName == null");
        }
        if (sqlTableSource instanceof SQLExprTableSource) {
            SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) sqlTableSource;
            SqlExprTable sqlExprTable = SqlNameParser.getSQLExprTable(sqlExprTableSource);
            if (targetTableName.equals(sqlTableSource.getAlias()) || targetTableName.equalsIgnoreCase(sqlExprTable.getTableName())) {
                return logicDbConfig.getLogicTableManager(sqlExprTable.getTableName()).getLogicTable().getColumns().keySet();
            }
            return null;
        } else if (sqlTableSource instanceof SQLJoinTableSource) {
            //从多个中选择一个，必须指定targetTableName
            SQLJoinTableSource joinTableSource = (SQLJoinTableSource) sqlTableSource;
            Set<String> ls = getAllColumns(joinTableSource.getLeft(), targetTableName);
            if (ls != null) {
                return ls;
            }
            return getAllColumns(joinTableSource.getRight(), targetTableName);
        }
        if (sqlTableSource.getAlias() == null) {
            throw new SqlParseException("sqlTableSource.getAlias() == null");
        }
        if (!sqlTableSource.getAlias().equals(targetTableName)) {
            return null;
        }
        if (sqlTableSource instanceof SQLSubqueryTableSource) {
            SQLSubqueryTableSource subqueryTableSource = (SQLSubqueryTableSource) sqlTableSource;
            SQLSelectQuery sqlSelectQuery = subqueryTableSource.getSelect().getQuery();
            if (sqlSelectQuery == null) {
                throw new UnsupportedSqlOperatorException("sqlSelectQuery == null");
            }
            return parseSelectLabels(sqlSelectQuery);

        } else if (sqlTableSource instanceof SQLUnionQueryTableSource) {
            SQLUnionQueryTableSource unionQueryTableSource = (SQLUnionQueryTableSource) sqlTableSource;
            return parseSelectLabels(unionQueryTableSource.getUnion());
        } else {
            throw new SqlParseException("无法识别的tableSource类型" + sqlTableSource.getClass().getName());
        }
    }

}
