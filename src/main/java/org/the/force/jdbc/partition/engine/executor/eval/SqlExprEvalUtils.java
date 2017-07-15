package org.the.force.jdbc.partition.engine.executor.eval;

import java.math.BigDecimal;

/**
 * Created by xuji on 2017/1/2.
 * 封装sql表达式的解析函数
 */
public class SqlExprEvalUtils {


//    public static Object getValue(SQLExpr sqlExpr, SqlValueEvalContext sqlValueEvalContext,LogicSqlParameterHolder logicSqlParameterHolder,List<DataItemRow> rows) throws
//        SQLException {
//        SqlExprEvalFunction valueFunction = SqlExprEvalFunctionFactory.getSingleton().matchSqlValueFunction(sqlExpr);
//        if (valueFunction == null) {
//            throw new RuntimeException("valueFunction==null");
//        }
//        Object value = valueFunction.getValue(sqlValueEvalContext,logicSqlParameterHolder,rows);
//        if (value == null) {
//            throw new RuntimeException("getValue==null");
//        }
//        return value;
//    }

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
