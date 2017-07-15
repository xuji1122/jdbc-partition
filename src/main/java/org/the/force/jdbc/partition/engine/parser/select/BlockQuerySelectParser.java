package org.the.force.jdbc.partition.engine.parser.select;

import org.the.force.jdbc.partition.engine.executor.dql.elements.AllColumnItem;
import org.the.force.jdbc.partition.engine.executor.dql.elements.Select;
import org.the.force.jdbc.partition.engine.executor.dql.elements.ValueExprItem;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.ParallelJoinedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.SubQueriedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.UnionQueriedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.WrappedSQLExprTableSource;
import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTableRefers;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlReferParser;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/13.
 */
public class BlockQuerySelectParser extends AbstractVisitor {

    private final LogicDbConfig logicDbConfig;

    private List<SqlRefer> groupByRefers;

    private List<SqlRefer> orderByRefers;

    //当从resultSet取value时
    private Map<SqlRefer, Integer> sqlPropertyIntegerMap = new HashMap<>();

    //key为tableSource的alias或者tableName
    private Map<String, SqlTable> sqlTableMap = new LinkedHashMap<>();

    private Map<SqlTable, SqlTableRefers> sqlTableRefersMap = new LinkedHashMap<>();

    private Map<SqlTable,Select> sqlTableSelectMap  = new LinkedHashMap<>();

    private Select gloableSelect;

    public BlockQuerySelectParser(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock) throws SQLException {
        this.logicDbConfig = logicDbConfig;

        List<SQLSelectItem> sqlSelectItems = sqlSelectQueryBlock.getSelectList();
        boolean distinctAll = sqlSelectQueryBlock.getDistionOption() > 0;

        SQLTableSource sqlTableSource = sqlSelectQueryBlock.getFrom();
        if (sqlTableSource instanceof ParallelJoinedTableSource) {
            ParallelJoinedTableSource tableSource = (ParallelJoinedTableSource) sqlTableSource;
            List<SqlTable> sqlTables = tableSource.getSqlTables();
            for (SqlTable sqlTable : sqlTables) {
                SqlTableRefers sqlTableRefers = new SqlTableReferParser(logicDbConfig, sqlSelectQueryBlock, sqlTable).getSqlTableRefers();
                sqlTableMap.put(sqlTable.getRelativeKey(), sqlTable);
                sqlTableRefersMap.put(sqlTable, sqlTableRefers);
                int index = 0;
                for (SQLSelectItem item : sqlSelectItems) {
                    String alias = item.getAlias();
                    SQLExpr sqlExpr = item.getExpr();
                    ValueExprItem valueExprItem = null;
                    if (sqlExpr instanceof SQLAllColumnExpr) {
                        valueExprItem = new AllColumnItem(sqlExpr, index++, null);
                    } else if (sqlExpr instanceof SQLName) {
                        SqlRefer sqlRefer = SqlReferParser.getSqlRefer(sqlExpr);
                        if (sqlRefer.getName().equals("*")) {
                            valueExprItem = new AllColumnItem(sqlRefer, index++, null);
                        } else {
                        }
                    } else if (sqlExpr instanceof SQLAggregateExpr) {

                    } else {
                        //TODO sqlMethod支持
                    }
                }
            }
        } else {
            SqlTable sqlTable = null;
            SqlTableRefers sqlTableRefers = null;
            if (sqlTableSource instanceof WrappedSQLExprTableSource) {
                WrappedSQLExprTableSource tableSource = (WrappedSQLExprTableSource) sqlTableSource;
                sqlTable = tableSource.getSqlTable();
                sqlTableRefers = new SqlTableReferParser(logicDbConfig, sqlSelectQueryBlock, sqlTable).getSqlTableRefers();
            } else if (sqlTableSource instanceof SubQueriedTableSource) {
                SubQueriedTableSource tableSource = (SubQueriedTableSource) sqlTableSource;
                 sqlTable = tableSource.getSqlTable();
                 sqlTableRefers = new SqlTableReferParser(logicDbConfig, sqlSelectQueryBlock, sqlTable).getSqlTableRefers();
            } else if (sqlTableSource instanceof UnionQueriedTableSource) {
                UnionQueriedTableSource tableSource = (UnionQueriedTableSource) sqlTableSource;
                 sqlTable = tableSource.getSqlTable();
                 sqlTableRefers = new SqlTableReferParser(logicDbConfig, sqlSelectQueryBlock, sqlTable).getSqlTableRefers();
            }else{
                throw new SqlParseException("tableSource is not converted");
            }
            gloableSelect = new Select(sqlTable,distinctAll);
            int index = 0;
            for (SQLSelectItem item : sqlSelectItems) {
                String alias = item.getAlias();
                SQLExpr sqlExpr = item.getExpr();
                ValueExprItem valueExprItem = null;
                if (sqlExpr instanceof SQLAllColumnExpr) {
                    valueExprItem = new AllColumnItem(sqlExpr, index++, null);
                } else if (sqlExpr instanceof SQLName) {
                    SqlRefer sqlRefer = SqlReferParser.getSqlRefer(sqlExpr);
                    if (sqlRefer.getName().equals("*")) {
                        valueExprItem = new AllColumnItem(sqlRefer, index++, null);
                    } else {
                    }
                } else if (sqlExpr instanceof SQLAggregateExpr) {

                } else {
                    //TODO sqlMethod支持
                }
            }
        }



    }





}
