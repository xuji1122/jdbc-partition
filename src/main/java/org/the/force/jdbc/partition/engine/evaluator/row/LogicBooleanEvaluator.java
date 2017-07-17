package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.exception.PartitionSystemException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/15.
 */
public class LogicBooleanEvaluator extends AbstractSqlExprEvaluator {

    private final SqlExprEvaluator left;
    private final SqlExprEvaluator right;
    private final SQLBinaryOperator operator;

    public LogicBooleanEvaluator(LogicDbConfig logicDbConfig, SQLBinaryOpExpr originalSqlExpr) {
        super(originalSqlExpr);
        left = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getLeft());
        right = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getRight());
        this.operator = originalSqlExpr.getOperator();
    }

    public Object eval(SqlExprEvalContext sqlExprEvalContext,  Object rows) throws SQLException {
        Object leftValue = this.left.eval(sqlExprEvalContext, rows);
        Object rightValue = this.right.eval(sqlExprEvalContext, rows);
        if (leftValue == null || rightValue == null) {
            return false;
        }
        if (!(leftValue instanceof Boolean)) {
            throw new PartitionSystemException("!(leftValue instanceof Boolean)");
        }
        if (!(rightValue instanceof Boolean)) {
            throw new PartitionSystemException("!(leftValue instanceof Boolean)");
        }
        if (operator == SQLBinaryOperator.BooleanAnd) {
            return ((Boolean) leftValue) && ((Boolean) rightValue);
        } else if (operator == SQLBinaryOperator.BooleanOr || operator == SQLBinaryOperator.BooleanXor) {
            return ((Boolean) leftValue) || ((Boolean) rightValue);
        }
        throw new PartitionSystemException("operator not match");
    }
}
