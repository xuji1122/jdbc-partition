package org.the.force.jdbc.partition.engine.evaluator;

import java.math.BigDecimal;

/**
 * Created by xuji on 2017/1/2.
 * 封装sql表达式的解析函数
 */
public class SqlExprEvalUtils {


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
