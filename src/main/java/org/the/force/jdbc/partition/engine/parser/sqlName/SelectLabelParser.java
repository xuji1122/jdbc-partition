package org.the.force.jdbc.partition.engine.parser.sqlName;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by xuji on 2017/6/14.
 */
public class SelectLabelParser {


    /**
     * TODO select *  select t.* 如何处理的问题
     *
     * @param selectList
     * @return
     */
    public Set<String> parseLabels(List<SQLSelectItem> selectList) {
        Set<String> columns = new LinkedHashSet<>();
        for (SQLSelectItem item : selectList) {
            String label = visit(item);
            if (label != null) {
                columns.add(label);
            }
        }
        return columns;
    }

    public String visit(SQLSelectItem item) {
        if (item.getAlias() != null) {
            return item.getAlias();
        }
        SQLExpr expr = item.getExpr();
        if (expr instanceof SQLName) {
            //SQLPropertyExpr  t.*  name=*
            SQLName sqlName = (SQLName) expr;
            return sqlName.getSimpleName();
        } else if (expr instanceof SQLAllColumnExpr) {
            // TODO select *  select t.* 如何处理的问题
        }
        return null;
    }
}
