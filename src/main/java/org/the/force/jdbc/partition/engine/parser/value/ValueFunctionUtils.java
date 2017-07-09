package org.the.force.jdbc.partition.engine.parser.value;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.jdbc.partition.exception.UnsupportedExprException;
import org.the.force.jdbc.partition.engine.parser.SqlValueEvalContext;

import java.math.BigDecimal;

/**
 * Created by xuji on 2017/1/2.
 * 封装sql表达式的解析函数
 */
public class ValueFunctionUtils {


    public static SqlValue getValue(SQLExpr sqlExpr, SqlValueEvalContext sqlValueEvalContext) throws UnsupportedExprException {
        SqlValueFunction valueFunction = SqlValueFunctionMatcher.getSingleton().matchSqlValueFunction(sqlExpr, sqlValueEvalContext);
        if (valueFunction == null) {
            throw new RuntimeException("valueFunction==null");
        }
        SqlValue value = valueFunction.getSqlValue(sqlExpr, sqlValueEvalContext);
        if (value == null) {
            throw new RuntimeException("getValue==null");
        }
        return value;
    }

    public static Object[] getLeftRightValue(SQLBinaryOpExpr sqlBinaryOpExpr, SqlValueEvalContext sqlValueEvalContext) throws UnsupportedExprException {
        SQLExpr leftSqlExpr = sqlBinaryOpExpr.getLeft();
        SQLExpr rightSqlExpr = sqlBinaryOpExpr.getRight();
        SqlValueFunction leftValueF = SqlValueFunctionMatcher.getSingleton().matchSqlValueFunction(leftSqlExpr, sqlValueEvalContext);
        SqlValueFunction rightValueF = SqlValueFunctionMatcher.getSingleton().matchSqlValueFunction(rightSqlExpr, sqlValueEvalContext);
        if (leftValueF == null || rightValueF == null) {
            throw new RuntimeException("leftValueF==null|| rightValueF==null");
        }
        Object leftValue = leftValueF.getSqlValue(leftSqlExpr, sqlValueEvalContext).getValue();
        Object rightValue = rightValueF.getSqlValue(rightSqlExpr, sqlValueEvalContext).getValue();
        if (leftValue == null || rightValue == null) {
            throw new RuntimeException("leftValue==null||rightValue==null");
        }
        return new Object[] {leftValue, rightValue};
    }

    public static BigDecimal[] tryToDecimal(Object leftValue, Object rightValue) {
        if ((leftValue instanceof Number) && (rightValue instanceof Number)) {
            Number n1 = (Number) leftValue;
            Number n2 = (Number) rightValue;
            BigDecimal l1 = new BigDecimal(n1.toString());
            BigDecimal l2 = new BigDecimal(n2.toString());
            return new BigDecimal[] {l1, l2};
        }
        return null;
    }

}
