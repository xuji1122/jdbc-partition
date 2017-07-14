package org.the.force.jdbc.partition.engine.parser.select;

import org.the.force.jdbc.partition.engine.executor.dql.elements.Select;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.ParallelJoinedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.SubQueriedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.UnionQueriedTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.WrappedSQLExprTableSource;
import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlReferParser;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.visitor.AbstractVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLMethodInvokeExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;

import java.sql.SQLException;
import java.util.HashMap;
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

    private Map<String,SqlTable> sqlTableHashMap  = new HashMap<>();

    private Select select;

    public BlockQuerySelectParser(LogicDbConfig logicDbConfig, SQLSelectQueryBlock sqlSelectQueryBlock) throws SQLException {
        this.logicDbConfig = logicDbConfig;
        List<SQLSelectItem> sqlSelectItems = sqlSelectQueryBlock.getSelectList();
        boolean distinctAll = sqlSelectQueryBlock.getDistionOption() > 0;
        select = new Select(distinctAll);

        SQLTableSource sqlTableSource = sqlSelectQueryBlock.getFrom();

        if (sqlTableSource instanceof WrappedSQLExprTableSource) {
            WrappedSQLExprTableSource wrappedSQLExprTableSource = (WrappedSQLExprTableSource)sqlSelectItems;
            SqlTable sqlTable = wrappedSQLExprTableSource.getSqlTable();
            new SqlTableReferParser(logicDbConfig, sqlSelectQueryBlock, sqlTable);
        } else if (sqlTableSource instanceof SubQueriedTableSource) {

        } else if (sqlTableSource instanceof UnionQueriedTableSource) {

        } else if (sqlTableSource instanceof ParallelJoinedTableSource) {

        }

        int index = 0;

        for (SQLSelectItem item : sqlSelectItems) {
            String alias = item.getAlias();

            SQLExpr sqlExpr = item.getExpr();
            if (sqlExpr instanceof SQLAllColumnExpr) {

            } else if (sqlExpr instanceof SQLName) {
                SqlRefer sqlRefer = SqlReferParser.getSqlRefer(sqlExpr);
                if (sqlRefer.getName().equals("*")) {

                }

            } else if (sqlExpr instanceof SQLAggregateExpr) {


            } else if (sqlExpr instanceof SQLMethodInvokeExpr) {
                //sqlMethod支持
            }
        }
    }

    public boolean visit(SQLAggregateExpr astNode) {
        return isContinue();
    }



}
