package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.exception.PartitionSystemException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;

import java.sql.SQLException;

import static org.the.force.thirdparty.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE_NULL;

/**
 * Created by xuji on 2017/7/15.
 */
public class RelationalBinaryOpEvaluator extends AbstractSqlExprEvaluator {

    private final SqlExprEvaluator left;
    private final SqlExprEvaluator right;
    private final SQLBinaryOperator operator;

    public RelationalBinaryOpEvaluator(LogicDbConfig logicDbConfig, SQLBinaryOpExpr originalSqlExpr) {
        super(originalSqlExpr);
        left = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getLeft());
        right = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getRight());
        this.operator = originalSqlExpr.getOperator();
    }

    public Object eval(SqlExprEvalContext sqlExprEvalContext, Object rows) throws SQLException {
        Object leftValue = this.left.eval(sqlExprEvalContext, rows);
        Object rightValue = this.right.eval(sqlExprEvalContext, rows);
        if (leftValue == null || rightValue == null) {
            return false;
        }
        if (operator == SQLBinaryOperator.NotEqual) {
            return !leftValue.equals(rightValue);
        }
        if (operator == SQLBinaryOperator.Is) {
            if (rightValue == EVAL_VALUE_NULL) {
                return true;
            }
            return null;
        } else if (operator == SQLBinaryOperator.IsNot) {
            if (rightValue == EVAL_VALUE_NULL) {
                return false;
            }
            return null;
        }
        if (!(leftValue instanceof Comparable<?>) || !(rightValue instanceof Comparable<?>)) {
            throw new PartitionSystemException("!(leftValue instanceof Comparable<?>) || !(rightValue instanceof Comparable<?>)");
        }
        Comparable<Object> c1 = (Comparable<Object>) leftValue;
        Comparable<Object> c2 = (Comparable<Object>) rightValue;
        int result = c1.compareTo(c2);
        switch (operator) {
            case GreaterThan:
                return result > 0;
            case GreaterThanOrEqual:
                return result >= 0;
            case LessThan:
                return result < 0;
            case LessThanOrEqual:
                return result <= 0;
            //TODO 各种条件判断  like not like
            default:
                throw new PartitionSystemException("not expected operator");
        }
    }
}
