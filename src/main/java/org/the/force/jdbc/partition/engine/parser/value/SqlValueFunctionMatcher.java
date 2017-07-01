package org.the.force.jdbc.partition.engine.parser.value;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.druid.sql.ast.expr.SQLBinaryOperator;
import org.druid.sql.ast.expr.SQLBooleanExpr;
import org.druid.sql.ast.expr.SQLDateExpr;
import org.druid.sql.ast.expr.SQLNullExpr;
import org.druid.sql.ast.expr.SQLNumericLiteralExpr;
import org.druid.sql.ast.expr.SQLTextLiteralExpr;
import org.druid.sql.ast.expr.SQLValuableExpr;
import org.druid.sql.ast.expr.SQLVariantRefExpr;
import org.druid.sql.visitor.SQLEvalVisitor;
import org.the.force.jdbc.partition.engine.parser.SqlParserContext;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by xuji on 2017/1/2.
 */
public class SqlValueFunctionMatcher {

    private static SqlValueFunctionMatcher singleton = new SqlValueFunctionMatcher();

    public static SqlValueFunctionMatcher getSingleton() {
        return singleton;
    }

    private Map<Class, SqlValueFunction> classMap = new LinkedHashMap<>();

    public SqlValueFunction matchSqlValueFunction(SQLExpr sqlExpr, SqlParserContext sqlParserContext) {
        for (Map.Entry<Class, SqlValueFunction> entry : classMap.entrySet()) {
            if (entry.getKey().isInstance(sqlExpr)) {
                return entry.getValue();
            }
        }
        if (sqlExpr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
            SqlValueFunction function = operatorMap.get(sqlBinaryOpExpr.getOperator());
            if (function != null) {
                return function;
            }
        }
        return null;
    }

    /**
     * vs SQLInListExpr
     */
    private Map<SQLBinaryOperator, SqlValueFunction> operatorMap = new LinkedHashMap<>();

    private SqlValueFunctionMatcher() {
        registerFunctions();

    }

    private void registerFunctions() {
        classMap.put(SQLValuableExpr.class, (sqlExpr, logicSqlContext) -> {
            SQLValuableExpr sqlValuableExpr = (SQLValuableExpr) sqlExpr;
            return new LiteralSqlValue(sqlValuableExpr.getValue());
        });
        classMap.put(SQLVariantRefExpr.class, (sqlExpr, logicSqlContext) -> {
            SQLVariantRefExpr variantRefExpr = (SQLVariantRefExpr) sqlExpr;
            return logicSqlContext.getLogicSqlParameterHolder().getSqlParameter(variantRefExpr.getIndex());
        });

        classMap.put(SQLNullExpr.class, (sqlExpr, logicSqlContext) -> new LiteralSqlValue(SQLEvalVisitor.EVAL_VALUE_NULL));

        classMap.put(SQLNumericLiteralExpr.class, (sqlExpr, logicSqlContext) -> {
            SQLNumericLiteralExpr sqlNumericLiteralExpr = (SQLNumericLiteralExpr) sqlExpr;
            return new LiteralSqlValue(sqlNumericLiteralExpr.getNumber());
        });

        classMap.put(SQLTextLiteralExpr.class, (sqlExpr, logicSqlContext) -> {
            SQLTextLiteralExpr sqlTextLiteralExpr = (SQLTextLiteralExpr) sqlExpr;
            return new LiteralSqlValue(sqlTextLiteralExpr.getText());
        });

        //java.plan.Timestamp sql的date类型 以java.util.Date为父类
        classMap.put(SQLDateExpr.class, (sqlExpr, logicSqlContext) -> {
            SQLDateExpr sqlDateExpr = (SQLDateExpr) (sqlExpr);
            return new LiteralSqlValue(sqlDateExpr.getLiteral());
        });

        classMap.put(SQLBooleanExpr.class, (sqlExpr, logicSqlContext) -> {
            SQLBooleanExpr sqlBooleanExpr = (SQLBooleanExpr) sqlExpr;
            return new LiteralSqlValue(sqlBooleanExpr.getValue());
        });

        //加法实现
        SqlValueFunction add = (sqlExpr, logicSqlContext) -> {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
            Object[] vs = ValueFunctionUtils.getLeftRightValue(sqlBinaryOpExpr, logicSqlContext);
            Object leftValue = vs[0];
            Object rightValue = vs[1];
            BigDecimal[] lr = ValueFunctionUtils.tryToDecimal(leftValue, rightValue);
            if (lr == null || lr.length == 0) {
                return new LiteralSqlValue(leftValue.toString() + rightValue.toString());
            } else {
                BigDecimal l1 = lr[0];
                BigDecimal l2 = lr[1];
                BigDecimal v = l1.add(l2);
                if (l1.scale() == 0 && l2.scale() == 0) {
                    return new LiteralSqlValue(v.longValue());
                } else {
                    return new LiteralSqlValue(v.doubleValue());
                }
            }
        };
        operatorMap.put(SQLBinaryOperator.Add, add);
        operatorMap.put(SQLBinaryOperator.Concat, add);
        //减法实现
        operatorMap.put(SQLBinaryOperator.Subtract, (sqlExpr, logicSqlContext) -> {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
            Object[] vs = ValueFunctionUtils.getLeftRightValue(sqlBinaryOpExpr, logicSqlContext);
            Object leftValue = vs[0];
            Object rightValue = vs[1];
            BigDecimal[] lr = ValueFunctionUtils.tryToDecimal(leftValue, rightValue);
            if (lr == null || lr.length == 0) {
                throw new RuntimeException("相减只能是数字");
            } else {
                BigDecimal l1 = lr[0];
                BigDecimal l2 = lr[1];
                BigDecimal v = l1.subtract(l2);
                if (l1.scale() == 0 && l2.scale() == 0) {
                    return new LiteralSqlValue(v.longValue());
                } else {
                    return new LiteralSqlValue(v.doubleValue());
                }
            }
        });
        //乘法
        operatorMap.put(SQLBinaryOperator.Multiply, (sqlExpr, logicSqlContext) -> {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
            Object[] vs = ValueFunctionUtils.getLeftRightValue(sqlBinaryOpExpr, logicSqlContext);
            Object leftValue = vs[0];
            Object rightValue = vs[1];
            BigDecimal[] lr = ValueFunctionUtils.tryToDecimal(leftValue, rightValue);
            if (lr == null || lr.length == 0) {
                throw new RuntimeException("相乘只能是数字");
            } else {
                BigDecimal l1 = lr[0];
                BigDecimal l2 = lr[1];
                BigDecimal v = l1.multiply(l2);
                if (l1.scale() == 0 && l2.scale() == 0) {
                    return new LiteralSqlValue(v.longValue());
                } else {
                    return new LiteralSqlValue(v.doubleValue());
                }
            }
        });
        //除法
        operatorMap.put(SQLBinaryOperator.Divide, (sqlExpr, logicSqlContext) -> {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
            Object[] vs = ValueFunctionUtils.getLeftRightValue(sqlBinaryOpExpr, logicSqlContext);
            Object leftValue = vs[0];
            Object rightValue = vs[1];
            BigDecimal[] lr = ValueFunctionUtils.tryToDecimal(leftValue, rightValue);
            if (lr == null || lr.length == 0) {
                throw new RuntimeException("相乘只能是数字");
            } else {
                BigDecimal l1 = lr[0];
                BigDecimal l2 = lr[1];
                if (l1.scale() == 0 && l2.scale() == 0) {
                    return new LiteralSqlValue(l1.longValue() / l2.longValue());
                } else {
                    return new LiteralSqlValue(l1.divide(l2, 4, BigDecimal.ROUND_HALF_UP));
                }
            }
        });
        //取模
        operatorMap.put(SQLBinaryOperator.Mod, (sqlExpr, logicSqlContext) -> {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
            Object[] vs = ValueFunctionUtils.getLeftRightValue(sqlBinaryOpExpr, logicSqlContext);
            Object leftValue = vs[0];
            Object rightValue = vs[1];
            BigDecimal[] lr = ValueFunctionUtils.tryToDecimal(leftValue, rightValue);
            if (lr == null || lr.length == 0) {
                throw new RuntimeException("相乘只能是数字");
            } else {
                BigDecimal l1 = lr[0];
                BigDecimal l2 = lr[1];
                if (l1.scale() == 0 && l2.scale() == 0) {
                    return new LiteralSqlValue(l1.longValue() % l2.longValue());
                } else {
                    throw new RuntimeException("取模只能是整数");
                }
            }
        });
    }

}

