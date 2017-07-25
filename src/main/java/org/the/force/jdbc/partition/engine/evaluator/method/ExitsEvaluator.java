package org.the.force.jdbc.partition.engine.evaluator.method;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.value.types.BooleanValue;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLMethodInvokeExpr;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/16.
 */
public class ExitsEvaluator extends AbstractMethodEvaluator {

    public ExitsEvaluator(LogicDbConfig logicDbConfig, SQLMethodInvokeExpr originalSqlExpr) {
        super(logicDbConfig, originalSqlExpr);
    }

    public BooleanValue eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        List<Object> value = evalArguments(sqlExprEvalContext, data);
        return new BooleanValue(value != null && !value.isEmpty());
    }

    public ExitsEvaluator(){

    }
}
