package org.the.force.jdbc.partition.engine.eval.functions;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.eval.SqlExprEvalFunction;
import org.the.force.jdbc.partition.engine.eval.SqlExprEvalFunctionFactory;
import org.the.force.jdbc.partition.engine.eval.SqlExprEvalUtils;
import org.the.force.jdbc.partition.engine.eval.SqlValueEvalContext;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/15.
 */
public class SQLBinaryOpEvalFunction extends SqlExprEvalFunction {

    private final SqlExprEvalFunction left;
    private final SqlExprEvalFunction right;
    private final SQLBinaryOperator operator;

    public SQLBinaryOpEvalFunction(SQLBinaryOpExpr originalSqlExpr) {
        super(originalSqlExpr);
        left = SqlExprEvalFunctionFactory.getSingleton().matchSqlValueFunction(originalSqlExpr.getLeft());
        right = SqlExprEvalFunctionFactory.getSingleton().matchSqlValueFunction(originalSqlExpr.getRight());
        this.operator = originalSqlExpr.getOperator();
    }

    public Object getValue(SqlValueEvalContext sqlValueEvalContext, LogicSqlParameterHolder logicSqlParameterHolder, Object rows) throws SQLException {
        Object leftValue = this.left.getValue(sqlValueEvalContext, logicSqlParameterHolder, rows);
        Object rightValue = this.right.getValue(sqlValueEvalContext, logicSqlParameterHolder, rows);
        if (operator == SQLBinaryOperator.Equality) {
            if (leftValue == null) {
                return false;
            } else {
                return leftValue.equals(rightValue);
            }
        }
        BigDecimal[] lr = SqlExprEvalUtils.tryToDecimal(leftValue, rightValue);
        if (operator == SQLBinaryOperator.Add || operator == SQLBinaryOperator.Concat) {
            if (lr == null || lr.length == 0) {
                return  leftValue.toString() + rightValue.toString();
            } else {
                BigDecimal l1 = lr[0];
                BigDecimal l2 = lr[1];
                BigDecimal v = l1.add(l2);
                if (l1.scale() == 0 && l2.scale() == 0) {
                    return v.longValue();
                } else {
                    return v.doubleValue();
                }
            }
        } else if (operator == SQLBinaryOperator.Subtract) {
            if (lr == null || lr.length == 0) {
                throw new RuntimeException("相减只能是数字");
            } else {
                BigDecimal l1 = lr[0];
                BigDecimal l2 = lr[1];
                BigDecimal v = l1.subtract(l2);
                if (l1.scale() == 0 && l2.scale() == 0) {
                    return v.longValue();
                } else {
                    return v.doubleValue();
                }
            }
        } else if (operator == SQLBinaryOperator.Multiply) {
            if (lr == null || lr.length == 0) {
                throw new RuntimeException("相乘只能是数字");
            } else {
                BigDecimal l1 = lr[0];
                BigDecimal l2 = lr[1];
                BigDecimal v = l1.multiply(l2);
                if (l1.scale() == 0 && l2.scale() == 0) {
                    return v.longValue();
                } else {
                    return v.doubleValue();
                }
            }
        } else if (operator == SQLBinaryOperator.Divide) {
            if (lr == null || lr.length == 0) {
                throw new RuntimeException("相除只能是数字");
            } else {
                BigDecimal l1 = lr[0];
                BigDecimal l2 = lr[1];
                return l1.divide(l2, 4, BigDecimal.ROUND_HALF_UP);
            }
        } else if (operator == SQLBinaryOperator.Mod) {
            if (lr == null || lr.length == 0) {
                throw new RuntimeException("取模只能是数字");
            } else {
                BigDecimal l1 = lr[0];
                BigDecimal l2 = lr[1];
                if (l1.scale() == 0 && l2.scale() == 0) {
                    return l1.longValue() % l2.longValue();
                } else {
                    throw new RuntimeException("取模只能是整数");
                }
            }
        }
        return null;
    }
}
