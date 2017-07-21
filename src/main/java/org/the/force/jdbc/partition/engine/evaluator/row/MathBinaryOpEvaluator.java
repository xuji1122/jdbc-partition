package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.value.SqlNull;
import org.the.force.jdbc.partition.engine.value.SqlNumber;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.exception.UnsupportedSqlOperatorException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/15.
 */
public class MathBinaryOpEvaluator extends AbstractSqlExprEvaluator {

    private final SqlExprEvaluator left;
    private final SqlExprEvaluator right;
    private final SQLBinaryOperator operator;

    public MathBinaryOpEvaluator(LogicDbConfig logicDbConfig, SQLBinaryOpExpr originalSqlExpr) {
        super(originalSqlExpr);
        left = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getLeft());
        right = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getRight());
        this.operator = originalSqlExpr.getOperator();
    }

    public SqlValue eval(SqlExprEvalContext sqlExprEvalContext, Object rows) throws SQLException {
        SqlValue leftValue = (SqlValue) this.left.eval(sqlExprEvalContext, rows);
        SqlValue rightValue = (SqlValue) this.right.eval(sqlExprEvalContext, rows);
        if (leftValue instanceof SqlNull || rightValue instanceof SqlNull) {
            throw new RuntimeException("SqlNull 不能用于算术运算");
        }
        if (operator == SQLBinaryOperator.Add) {
            return leftValue.add(rightValue);
        } else if (operator == SQLBinaryOperator.Subtract) {
            return leftValue.subtract(rightValue);
        } else if (operator == SQLBinaryOperator.Multiply) {
            return leftValue.multiply(rightValue);
        } else if (operator == SQLBinaryOperator.Divide) {
            return leftValue.divide(rightValue);
        } else if (operator == SQLBinaryOperator.Mod) {
            return leftValue.mod(rightValue);
        }
        throw new UnsupportedOperationException(SQLUtils.toMySqlString(getOriginalSqlExpr()));
    }
}
