package org.the.force.jdbc.partition.engine.evaluator.row;

import com.google.common.collect.Lists;
import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.types.BooleanValue;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBetweenExpr;

import java.sql.SQLException;
import java.util.List;

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

    public SqlValue eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        Comparable<Object> testValue = (Comparable<Object>) testSqlExprEvaluator.eval(sqlExprEvalContext, data);
        Comparable<Object> beginValue = (Comparable<Object>) beginSqlExprEvaluator.eval(sqlExprEvalContext, data);
        Comparable<Object> endValue = (Comparable<Object>) endSqlExprEvaluator.eval(sqlExprEvalContext, data);
        if (testValue == null) {
            return new BooleanValue(not);
        }
        if (testValue.compareTo(beginValue) < 0) {
            return new BooleanValue(not);
        }
        if (testValue.compareTo(endValue) > 0) {
            return new BooleanValue(not);
        }
        return new BooleanValue(!not);
    }

    public List<SqlExprEvaluator> children() {
        return Lists.newArrayList(testSqlExprEvaluator,beginSqlExprEvaluator,endSqlExprEvaluator);
    }
}
