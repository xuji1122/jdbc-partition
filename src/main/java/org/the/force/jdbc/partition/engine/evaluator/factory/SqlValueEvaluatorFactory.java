package org.the.force.jdbc.partition.engine.evaluator.factory;

import org.the.force.jdbc.partition.engine.parameter.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.evaluator.row.SqlValueEvaluator;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBooleanExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLDateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNumericLiteralExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLTextLiteralExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLTimestampExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLValuableExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLVariantRefExpr;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/16.
 */
public class SqlValueEvaluatorFactory {

    private final LogicDbConfig logicDbConfig;

    public SqlValueEvaluatorFactory(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    public SqlValueEvaluator matchSqlExprEvalFunction(SQLExpr sqlExpr) {
        //SQLUtils.isValue(sqlExpr);
        //SQL直接量和SQL参数Evaluator
        if (sqlExpr instanceof SQLValuableExpr) {//包括SQLNullExpr
            return new SqlValueEvaluator(sqlExpr) {
                public Object eval(LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
                    SQLValuableExpr sqlValuableExpr = (SQLValuableExpr) getOriginalSqlExpr();
                    return sqlValuableExpr.getValue();
                }
            };
        } else if (sqlExpr instanceof SQLVariantRefExpr) {
            return new SqlValueEvaluator(sqlExpr) {
                public Object eval(LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
                    SQLVariantRefExpr variantRefExpr = (SQLVariantRefExpr) getOriginalSqlExpr();
                    return logicSqlParameterHolder.getSqlParameter(variantRefExpr.getIndex()).getValue();
                }
            };
        } else if (sqlExpr instanceof SQLNumericLiteralExpr) {
            return new SqlValueEvaluator(sqlExpr) {
                public Object eval(LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
                    SQLNumericLiteralExpr sqlNumericLiteralExpr = (SQLNumericLiteralExpr) getOriginalSqlExpr();
                    return sqlNumericLiteralExpr.getNumber();
                }
            };
        } else if (sqlExpr instanceof SQLTextLiteralExpr) {
            return new SqlValueEvaluator(sqlExpr) {
                public Object eval(LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
                    SQLTextLiteralExpr sqlTextLiteralExpr = (SQLTextLiteralExpr) getOriginalSqlExpr();
                    return sqlTextLiteralExpr.getText();
                }
            };
        } else if (sqlExpr instanceof SQLDateExpr) {
            return new SqlValueEvaluator(sqlExpr) {
                public Object eval(LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
                    SQLDateExpr sqlDateExpr = (SQLDateExpr) getOriginalSqlExpr();
                    return sqlDateExpr.getLiteral();
                }
            };
        } else if (sqlExpr instanceof SQLTimestampExpr) {
            return new SqlValueEvaluator(sqlExpr) {
                public Object eval(LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
                    SQLTimestampExpr sqlDateExpr = (SQLTimestampExpr) getOriginalSqlExpr();
                    return sqlDateExpr.getLiteral();
                }
            };
        } else if (sqlExpr instanceof SQLBooleanExpr) {
            return new SqlValueEvaluator(sqlExpr) {
                public Object eval(LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
                    SQLBooleanExpr sqlBooleanExpr = (SQLBooleanExpr) getOriginalSqlExpr();
                    return sqlBooleanExpr.getValue();
                }
            };
            //SQL直接量和参数Evaluator结束
        }
        return null;
    }
}
