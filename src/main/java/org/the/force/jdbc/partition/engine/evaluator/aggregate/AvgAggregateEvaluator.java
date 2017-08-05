package org.the.force.jdbc.partition.engine.evaluator.aggregate;

import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/13.
 */
public class AvgAggregateEvaluator extends AggregateEvaluator {


    public AvgAggregateEvaluator(LogicDbConfig logicDbConfig, SQLAggregateExpr sqlAggregateExpr) {
        super(logicDbConfig, sqlAggregateExpr);
    }

    public AvgAggregateEvaluator() {
    }

    public Object eval(SqlLineExecRequest sqlLineExecRequest,  Object rows) throws SQLException {
        return null;
    }


}
