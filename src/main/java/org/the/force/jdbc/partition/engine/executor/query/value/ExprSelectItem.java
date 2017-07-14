package org.the.force.jdbc.partition.engine.executor.query.value;

import org.the.force.jdbc.partition.engine.executor.query.elements.SelectItem;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

/**
 * Created by xuji on 2017/7/13.
 */
public class ExprSelectItem extends SelectItem implements ReferValueFunction {


    public ExprSelectItem(SQLExpr sqlExpr, int index, String label) {
        super(sqlExpr, index, label);
    }



}
