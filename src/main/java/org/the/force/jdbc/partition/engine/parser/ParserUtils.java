package org.the.force.jdbc.partition.engine.parser;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;

/**
 * Created by xuji on 2017/6/14.
 */
public class ParserUtils {

    public static boolean isRelational(SQLExpr sqlExpr) {
        if (!(sqlExpr instanceof SQLBinaryOpExpr)) {
            return false;
        }
        SQLBinaryOpExpr opExpr = (SQLBinaryOpExpr) sqlExpr;
        return isRelational(opExpr);
    }

    public static boolean isRelational(SQLBinaryOpExpr opExpr) {
        return isRelational(opExpr.getOperator());
    }

    public static boolean isRelational(SQLBinaryOperator operator) {
        return operator.isRelational() || operator == SQLBinaryOperator.Is || operator == SQLBinaryOperator.IsNot;
    }


    public static boolean isLogical(SQLExpr sqlExpr) {
        if (!(sqlExpr instanceof SQLBinaryOpExpr)) {
            return false;
        }
        SQLBinaryOpExpr opExpr = (SQLBinaryOpExpr) sqlExpr;
        return isLogical(opExpr);
    }

    public static boolean isLogical(SQLBinaryOpExpr opExpr) {
        return isLogical(opExpr.getOperator());
    }
    public static boolean isLogical(SQLBinaryOperator operator) {
        return operator.isLogical();
    }
}
