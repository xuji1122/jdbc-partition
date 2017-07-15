package org.the.force.jdbc.partition.engine.executor.eval.functions;

import org.the.force.jdbc.partition.engine.executor.eval.SqlExprEvalFunction;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/14.
 */
public abstract class AggregateFunction extends SqlExprEvalFunction {

    private List<SqlExprEvalFunction> arguments = null;

    public AggregateFunction(SQLAggregateExpr sqlExpr) {
        super(sqlExpr);
    }

    public List<SqlExprEvalFunction> getArguments() {
        if (arguments == null) {
            arguments = new ArrayList<>();
        }
        return arguments;
    }

}
