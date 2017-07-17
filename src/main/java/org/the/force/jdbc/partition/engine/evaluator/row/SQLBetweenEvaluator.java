package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBetweenExpr;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/16.
 */
public class SQLBetweenEvaluator extends AbstractSqlExprEvaluator {

    private boolean not;

    private final SqlExprEvaluator testSqlExprEvaluator;

    private final SqlExprEvaluator beginSqlExprEvaluator;

    private final SqlExprEvaluator endSqlExprEvaluator;

    public SQLBetweenEvaluator(LogicDbConfig logicDbConfig, SQLBetweenExpr originalSqlExpr) {
        super(originalSqlExpr);
        this.not = originalSqlExpr.isNot();
        this.testSqlExprEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getTestExpr());
        beginSqlExprEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getBeginExpr());
        endSqlExprEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getEndExpr());
    }

    public Object eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        Comparable<Object> testValue = (Comparable<Object>) testSqlExprEvaluator.eval(sqlExprEvalContext, data);
        Comparable<Object> beginValue = (Comparable<Object>) beginSqlExprEvaluator.eval(sqlExprEvalContext, data);
        Comparable<Object> endValue = (Comparable<Object>) endSqlExprEvaluator.eval(sqlExprEvalContext, data);
        if (testValue == null) {
            return not;
        }
        if (testValue.compareTo(beginValue) < 0) {
            return not;
        }
        if (testValue.compareTo(endValue) > 0) {
            return not;
        }
        return !not;
    }
}
