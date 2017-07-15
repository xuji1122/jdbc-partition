package org.the.force.jdbc.partition.engine.executor.dql.elements;

import org.the.force.jdbc.partition.engine.result.DataItemRow;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

/**
 * Created by xuji on 2017/7/14.
 */
public class ValueExprItem extends ValueExpr{

    private final int index;

    private final String label;

    public ValueExprItem(SQLExpr sqlExpr, int index, String label) {
        super(sqlExpr);
        this.index = index;
        this.label = label;
    }

    public int getIndex() {
        return index;
    }

    public String getLabel() {
        return label;
    }

    public Object getValue(DataItemRow rows) {
        return rows.getValue(getIndex());
    }


}
