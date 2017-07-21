package org.the.force.jdbc.partition.engine.evaluator.factory;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SqlLiteralEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SqlParameterEvaluator;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.literal.BigIntegerValue;
import org.the.force.jdbc.partition.engine.value.literal.LiteralDecimal;
import org.the.force.jdbc.partition.engine.value.literal.LiteralNull;
import org.the.force.jdbc.partition.engine.value.literal.TextValue;
import org.the.force.jdbc.partition.engine.value.types.BooleanValue;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBooleanExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLDateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIntegerExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNullExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNumericLiteralExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLTextLiteralExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLTimestampExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLValuableExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLVariantRefExpr;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/16.
 */
public class SqlValueEvaluatorFactory {

    public SqlValueEvaluatorFactory() {

    }

    public SqlExprEvaluator matchSqlExprEvaluator(SQLExpr sqlExpr) {
        //参考 SQLUtils.isValue(sqlExpr);
        /*
            最常见三个优先匹配处理
         */
        if (sqlExpr instanceof SQLVariantRefExpr) {
            return new SqlParameterEvaluator((SQLVariantRefExpr) sqlExpr);
        } else if (sqlExpr instanceof SQLIntegerExpr) {
            return new SqlLiteralEvaluator(sqlExpr) {
                public SqlValue eval() throws SQLException {
                    SQLIntegerExpr sqlIntegerExpr = (SQLIntegerExpr) sqlExpr;
                    return new BigIntegerValue(new BigInteger(sqlIntegerExpr.getNumber().toString()));
                }
            };
        } else if (sqlExpr instanceof SQLTextLiteralExpr) {
            return new SqlLiteralEvaluator(sqlExpr) {
                public SqlValue eval() throws SQLException {
                    SQLTextLiteralExpr sqlTextLiteralExpr = (SQLTextLiteralExpr) getOriginalSqlExpr();
                    return new TextValue(sqlTextLiteralExpr.getText());
                }
            };
        } else if (sqlExpr instanceof SQLNullExpr) {
            return new SqlLiteralEvaluator(sqlExpr) {
                public SqlValue eval() throws SQLException {
                    return new LiteralNull();
                }
            };
        } else if (sqlExpr instanceof SQLValuableExpr) {
            return new SqlLiteralEvaluator(sqlExpr) {
                public SqlValue eval() throws SQLException {
                    SQLValuableExpr sqlValuableExpr = (SQLValuableExpr) getOriginalSqlExpr();
                    return new TextValue(sqlValuableExpr.getValue().toString());
                }
            };
        } else if (sqlExpr instanceof SQLNumericLiteralExpr) {
            return new SqlLiteralEvaluator(sqlExpr) {
                public SqlValue eval() throws SQLException {
                    SQLNumericLiteralExpr sqlNumericLiteralExpr = (SQLNumericLiteralExpr) getOriginalSqlExpr();
                    Number number = sqlNumericLiteralExpr.getNumber();
                    if (sqlNumericLiteralExpr instanceof SQLIntegerExpr) {
                        return new BigIntegerValue(new BigInteger(number.toString()));
                    } else {
                        return new LiteralDecimal(new BigDecimal(number.toString()));
                    }
                }
            };
        } else if (sqlExpr instanceof SQLDateExpr) {
            return new SqlLiteralEvaluator(sqlExpr) {
                public SqlValue eval() throws SQLException {
                    SQLDateExpr sqlDateExpr = (SQLDateExpr) getOriginalSqlExpr();
                    return new TextValue(sqlDateExpr.getLiteral());
                }
            };
        } else if (sqlExpr instanceof SQLTimestampExpr) {
            return new SqlLiteralEvaluator(sqlExpr) {
                public SqlValue eval() throws SQLException {
                    SQLTimestampExpr sqlDateExpr = (SQLTimestampExpr) getOriginalSqlExpr();
                    return new TextValue(sqlDateExpr.getLiteral());
                }
            };
        } else if (sqlExpr instanceof SQLBooleanExpr) {
            return new SqlLiteralEvaluator(sqlExpr) {
                public SqlValue eval() throws SQLException {
                    SQLBooleanExpr sqlBooleanExpr = (SQLBooleanExpr) getOriginalSqlExpr();
                    return new BooleanValue(sqlBooleanExpr.getValue());
                }
            };
        }
        return null;
    }
}
