package org.the.force.jdbc.partition.engine.executor.query.value;

import org.the.force.jdbc.partition.engine.result.DataItemRow;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;

import java.util.List;

/**
 * Created by xuji on 2017/7/13.
 */
public class MaxAggregate extends AggregateBase implements SelfAggregateFunction {


    public MaxAggregate(SQLAggregateExpr sqlExpr, int index, String label) {
        super(sqlExpr, index, label);
    }

    @Override
    public Object getValue(List<DataItemRow> rows) {
        return null;
    }
}
