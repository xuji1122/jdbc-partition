package org.the.force.jdbc.partition.engine.parser.select;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.factory.SqlExprEvaluatorFactory;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SelectLabelParser;
import org.the.force.jdbc.partition.engine.sql.SqlRefer;
import org.the.force.jdbc.partition.engine.sql.query.SelectTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/25.
 */
public class SelectItemParser {

    private final LogicDbConfig logicDbConfig;

    public SelectItemParser(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }


    public void extendAllColumns(SQLSelectQueryBlock sqlSelectQueryBlock, SQLTableSource sqlTableSource) {
        SelectLabelParser selectLabelParser = new SelectLabelParser(logicDbConfig);
        List<SQLSelectItem> sqlSelectItems = sqlSelectQueryBlock.getSelectList();
        int size = sqlSelectItems.size();
        List<SQLSelectItem> newItems = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            SQLSelectItem oldItem = sqlSelectItems.get(i);
            SQLExpr itemExpr = oldItem.getExpr();
            if (itemExpr instanceof SQLAllColumnExpr) {
                List<String> labels = selectLabelParser.getAllColumns(sqlTableSource, null);
                if (labels == null || labels.isEmpty()) {
                    newItems.add(oldItem);
                    continue;
                }
                for (String label : labels) {
                    SQLSelectItem newItem = new SQLSelectItem();
                    newItems.add(newItem);
                    if (sqlTableSource.getAlias() != null) {
                        newItem.setExpr(new SQLPropertyExpr(sqlTableSource.getAlias(), label));
                    } else {
                        newItem.setExpr(new SQLIdentifierExpr(label));
                    }
                }
            } else if (itemExpr instanceof SQLPropertyExpr) {
                SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) itemExpr;
                SqlRefer sqlRefer = new SqlRefer(sqlPropertyExpr);
                if (sqlRefer.getName().equals("*")) {
                    List<String> labels = selectLabelParser.getAllColumns(sqlTableSource, sqlRefer.getOwnerName());
                    if (labels == null || labels.isEmpty()) {
                        newItems.add(oldItem);
                        continue;
                    }
                    for (String label : labels) {
                        SQLSelectItem newItem = new SQLSelectItem();
                        newItems.add(newItem);
                        newItem.setExpr(new SQLPropertyExpr(sqlRefer.getOwnerName(), label));
                    }
                } else {
                    newItems.add(oldItem);
                }
            } else {
                newItems.add(oldItem);
            }
        }
        sqlSelectQueryBlock.getSelectList().clear();
        sqlSelectQueryBlock.getSelectList().addAll(newItems);
    }

    public void parseItems(SQLSelectQueryBlock sqlSelectQueryBlock, SelectTable selectTable) {
        List<SQLSelectItem> sqlSelectItems = sqlSelectQueryBlock.getSelectList();
        int size = sqlSelectItems.size();
        SqlExprEvaluatorFactory sqlExprEvaluatorFactory = logicDbConfig.getSqlExprEvaluatorFactory();
        for (int i = 0; i < size; i++) {
            SQLSelectItem item = sqlSelectItems.get(i);
            SQLExpr itemExpr = item.getExpr();
            SqlRefer sqlRefer = null;
            SqlExprEvaluator sqlExprEvaluator;
            if (itemExpr instanceof SQLAllColumnExpr) {
                selectTable.addAllColumnItem(item);
                continue;
            } else if (itemExpr instanceof SQLPropertyExpr) {
                SQLPropertyExpr sqlPropertyExpr = (SQLPropertyExpr) itemExpr;
                sqlRefer = new SqlRefer(sqlPropertyExpr);
                if (sqlRefer.getName().equals("*")) {
                    selectTable.addAllColumnItem(item);
                    continue;
                }
            } else if (itemExpr instanceof SQLName) {
                SQLName sqlName = (SQLName) itemExpr;
                sqlRefer = new SqlRefer(sqlName);
            }
            if (sqlRefer != null) {
                sqlExprEvaluator = sqlRefer;
            } else {
                sqlExprEvaluator = sqlExprEvaluatorFactory.matchSqlExprEvaluator(itemExpr);
            }
            selectTable.addValueNode(item, sqlExprEvaluator);
        }
    }
}
