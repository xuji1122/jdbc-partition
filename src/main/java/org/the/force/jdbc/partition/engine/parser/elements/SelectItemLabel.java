package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlReferParser;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;

/**
 * Created by xuji on 2017/7/13.
 */
public class SelectItemLabel {

    private final SQLExpr expr;//row 函数或者SQLPropertyExpr也可能是聚合查询
    private final SqlRefer sqlRefer;//当row函数或者聚合函数时为null
    private final String label;


    public SelectItemLabel(SQLSelectItem sqlSelectItem) {
        this.expr = sqlSelectItem.getExpr();
        if (sqlSelectItem.getAlias() != null) {
            this.label = sqlSelectItem.getAlias();
        } else {
            this.label = null;
        }
        sqlRefer = SqlReferParser.getSqlRefer(expr);
    }

    public SqlRefer getSqlRefer() {
        return sqlRefer;
    }

    public String getLabel() {
        return label;
    }


    public SQLExpr getExpr() {
        return expr;
    }

}
