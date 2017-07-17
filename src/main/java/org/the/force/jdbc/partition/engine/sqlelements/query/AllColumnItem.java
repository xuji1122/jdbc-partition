package org.the.force.jdbc.partition.engine.sqlelements.query;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

/**
 * Created by xuji on 2017/7/15.
 */
public class AllColumnItem extends ValueExprItem {

    public AllColumnItem(SQLExpr sqlExpr, int index, String label) {
        super(sqlExpr, index, label);
    }

}
