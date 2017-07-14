package org.the.force.jdbc.partition.engine.executor.query.value;

import org.the.force.jdbc.partition.engine.executor.query.elements.SelectItem;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/14.
 */
public abstract class AggregateBase extends SelectItem implements SelfAggregateFunction,ReferValueFunction {



    private List<SQLExpr> arguments = null;

    public AggregateBase(SQLExpr sqlExpr, int index, String label) {
        super(sqlExpr, index, label);
    }


    public List<SQLExpr> getArguments() {
        if (arguments == null) {
            arguments = new ArrayList<>();
        }
        return arguments;
    }


}
