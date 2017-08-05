package org.the.force.jdbc.partition.engine.evaluator.aggregate;

import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/13.
 */
public class MaxAggregateEvaluator extends AggregateEvaluator {

    public MaxAggregateEvaluator(LogicDbConfig logicDbConfig, SQLAggregateExpr sqlAggregateExpr) {
        super(logicDbConfig, sqlAggregateExpr);
    }

    public MaxAggregateEvaluator() {
    }

    public Object eval(SqlLineExecRequest sqlLineExecRequest,  Object rows) throws SQLException {
        return null;
    }

}
