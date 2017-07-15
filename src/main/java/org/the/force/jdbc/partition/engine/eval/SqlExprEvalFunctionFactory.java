package org.the.force.jdbc.partition.engine.eval;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.eval.functions.SQLBinaryOpEvalFunction;
import org.the.force.jdbc.partition.engine.eval.functions.SqlReferEvalFunction;
import org.the.force.jdbc.partition.engine.eval.functions.SqlValueEvalFunction;
import org.the.force.jdbc.partition.exception.UnsupportedExprException;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBooleanExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLDateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNumericLiteralExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLTextLiteralExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLValuableExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLVariantRefExpr;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by xuji on 2017/1/2.
 */
public class SqlExprEvalFunctionFactory {

    private static SqlExprEvalFunctionFactory singleton = new SqlExprEvalFunctionFactory();

    public static SqlExprEvalFunctionFactory getSingleton() {
        return singleton;
    }

    private Map<Class, SqlValueEvalFunction> classMap = new LinkedHashMap<>();

    public SqlExprEvalFunction matchSqlValueFunction(SQLExpr sqlExpr) {
        //TODO 参考
        SQLUtils.isValue(sqlExpr);
        if (sqlExpr instanceof SQLValuableExpr) {//包括SQLNullExpr
            return new SqlValueEvalFunction(sqlExpr) {
                public Object getValue(SqlValueEvalContext sqlValueEvalContext, LogicSqlParameterHolder logicSqlParameterHolder, Object rows)
                    throws UnsupportedExprException {
                    SQLValuableExpr sqlValuableExpr = (SQLValuableExpr) getOriginalSqlExpr();
                    return sqlValuableExpr.getValue();
                }
            };
        } else if (sqlExpr instanceof SQLVariantRefExpr) {
            return new SqlValueEvalFunction(sqlExpr) {
                public Object getValue(SqlValueEvalContext sqlValueEvalContext, LogicSqlParameterHolder logicSqlParameterHolder, Object rows)
                    throws UnsupportedExprException {
                    SQLVariantRefExpr variantRefExpr = (SQLVariantRefExpr) getOriginalSqlExpr();
                    return logicSqlParameterHolder.getSqlParameter(variantRefExpr.getIndex()).getValue();
                }
            };
        } else if (sqlExpr instanceof SQLNumericLiteralExpr) {
            return new SqlValueEvalFunction(sqlExpr) {
                public Object getValue(SqlValueEvalContext sqlValueEvalContext, LogicSqlParameterHolder logicSqlParameterHolder, Object rows)
                    throws UnsupportedExprException {
                    SQLNumericLiteralExpr sqlNumericLiteralExpr = (SQLNumericLiteralExpr) getOriginalSqlExpr();
                    return sqlNumericLiteralExpr.getNumber();
                }
            };
        } else if (sqlExpr instanceof SQLTextLiteralExpr) {
            return new SqlValueEvalFunction(sqlExpr) {
                public Object getValue(SqlValueEvalContext sqlValueEvalContext, LogicSqlParameterHolder logicSqlParameterHolder, Object rows)
                    throws UnsupportedExprException {
                    SQLTextLiteralExpr sqlTextLiteralExpr = (SQLTextLiteralExpr) getOriginalSqlExpr();
                    return sqlTextLiteralExpr.getText();
                }
            };
        } else if (sqlExpr instanceof SQLDateExpr) {
            return new SqlValueEvalFunction(sqlExpr) {
                public Object getValue(SqlValueEvalContext sqlValueEvalContext, LogicSqlParameterHolder logicSqlParameterHolder, Object rows)
                    throws SQLException {
                    SQLDateExpr sqlDateExpr = (SQLDateExpr) getOriginalSqlExpr();
                    return sqlDateExpr.getLiteral();
                }
            };
        } else if (sqlExpr instanceof SQLBooleanExpr) {
            return new SqlValueEvalFunction(sqlExpr) {
                public Object getValue(SqlValueEvalContext sqlValueEvalContext, LogicSqlParameterHolder logicSqlParameterHolder, Object rows) throws SQLException {
                    SQLBooleanExpr sqlBooleanExpr = (SQLBooleanExpr) getOriginalSqlExpr();
                    return sqlBooleanExpr.getValue();
                }
            };
        } else if (sqlExpr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr sqlBinaryOpExpr = (SQLBinaryOpExpr) sqlExpr;
            return new SQLBinaryOpEvalFunction(sqlBinaryOpExpr);
        } else if (sqlExpr instanceof SQLName) {
            return new SqlReferEvalFunction((SQLName) sqlExpr);
        }
        return null;
    }

}

