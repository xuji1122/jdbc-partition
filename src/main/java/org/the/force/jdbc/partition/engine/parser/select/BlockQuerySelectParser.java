package org.the.force.jdbc.partition.engine.parser.select;

import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.SqlRefer;
import org.the.force.jdbc.partition.engine.sql.SqlTableRefers;
import org.the.force.jdbc.partition.engine.sql.query.Select;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;

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
    private Map<String, ConditionalSqlTable> sqlTableMap = new LinkedHashMap<>();

    private Map<ConditionalSqlTable, SqlTableRefers> sqlTableRefersMap = new LinkedHashMap<>();

    private Map<ConditionalSqlTable, Select> sqlTableSelectMap = new LinkedHashMap<>();

    private Select gloableSelect;

    public BlockQuerySelectParser(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock) throws SQLException {
        this.logicDbConfig = logicDbConfig;

        //        List<SQLSelectItem> sqlSelectItems = sqlSelectQueryBlock.getSelectList();
        //        boolean distinctAll = sqlSelectQueryBlock.getDistionOption() > 0;
        //
        //        SQLTableSource sqlTableSource = sqlSelectQueryBlock.getFrom();
        //        if (sqlTableSource instanceof JoinedTableSourceFactory) {
        //            JoinedTableSourceFactory tableSource = (JoinedTableSourceFactory) sqlTableSource;
        //            List<ConditionalSqlTable> sqlTables = tableSource.getSqlTables();
        //            for (ConditionalSqlTable sqlTable : sqlTables) {
        //                SqlTableRefers sqlTableRefers = new SqlTableReferParser(logicDbConfig, sqlSelectQueryBlock, sqlTable).getSqlTableRefers();
        //                sqlTableMap.put(sqlTable.getRelativeKey(), sqlTable);
        //                sqlTableRefersMap.put(sqlTable, sqlTableRefers);
        //                int index = 0;
        //                for (SQLSelectItem item : sqlSelectItems) {
        //                    String alias = item.getAlias();
        //                    SQLExpr sqlExpr = item.getExpr();
        //                    ValueExprItem valueExprItem = null;
        //                    if (sqlExpr instanceof SQLAllColumnExpr) {
        //                        valueExprItem = new AllColumnItem(sqlExpr, index++, null);
        //                    } else if (sqlExpr instanceof SQLName) {
        //                        SqlRefer sqlRefer = new SqlRefer((SQLName) sqlExpr);
        //                        if (sqlRefer.getName().equals("*")) {
        //                            valueExprItem = new AllColumnItem(sqlRefer, index++, null);
        //                        } else {
        //                        }
        //                    } else if (sqlExpr instanceof SQLAggregateExpr) {
        //
        //                    } else {
        //                        //TODO sqlMethod支持
        //                    }
        //                }
        //            }
        //        } else {
        //            ConditionalSqlTable sqlTable = null;
        //            gloableSelect = new Select(sqlTable, distinctAll);
        //            int index = 0;
        //            for (SQLSelectItem item : sqlSelectItems) {
        //                String alias = item.getAlias();
        //                SQLExpr sqlExpr = item.getExpr();
        //                ValueExprItem valueExprItem = null;
        //                if (sqlExpr instanceof SQLAllColumnExpr) {
        //                    valueExprItem = new AllColumnItem(sqlExpr, index++, null);
        //                } else if (sqlExpr instanceof SQLName) {
        //                    SqlRefer sqlRefer = new SqlRefer((SQLName) sqlExpr);
        //                    if (sqlRefer.getName().equals("*")) {
        //                        valueExprItem = new AllColumnItem(sqlRefer, index++, null);
        //                    } else {
        //                    }
        //                } else if (sqlExpr instanceof SQLAggregateExpr) {
        //
        //                } else {
        //                    //TODO sqlMethod支持
        //                }
        //            }
        //        }

    }



}
