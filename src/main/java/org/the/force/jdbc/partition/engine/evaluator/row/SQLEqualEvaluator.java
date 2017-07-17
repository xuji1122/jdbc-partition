package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/15.
 */
public class SQLEqualEvaluator extends AbstractSqlExprEvaluator {

    private final SqlExprEvaluator leftEvaluator;
    private final SqlExprEvaluator rightEvaluator;

    public SQLEqualEvaluator(LogicDbConfig logicDbConfig, SQLBinaryOpExpr originalSqlExpr) {
        super(originalSqlExpr);
        leftEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getLeft());
        rightEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getRight());
    }

    public Object eval(SqlExprEvalContext sqlExprEvalContext, Object rows) throws SQLException {
        Object leftValue = this.leftEvaluator.eval(sqlExprEvalContext, rows);
        Object rightValue = this.rightEvaluator.eval(sqlExprEvalContext, rows);
        if (leftValue == null || rightValue == null) {
            return false;
        }
        return leftValue.equals(rightValue);
    }

    public SqlExprEvaluator getLeftEvaluator() {
        return leftEvaluator;
    }

    public SqlExprEvaluator getRightEvaluator() {
        return rightEvaluator;
    }
}
