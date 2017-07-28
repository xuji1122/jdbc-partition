package org.the.force.jdbc.partition.engine.evaluator.row;

import com.google.common.collect.Lists;
import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.executor.SqlExecutionContext;
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

    private  SqlExprEvaluator testSqlExprEvaluator;

    private  SqlExprEvaluator beginSqlExprEvaluator;

    private  SqlExprEvaluator endSqlExprEvaluator;

    public SQLBetweenEvaluator(LogicDbConfig logicDbConfig, SQLBetweenExpr originalSqlExpr) {
        super(originalSqlExpr);
        this.not = originalSqlExpr.isNot();
        this.testSqlExprEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getTestExpr());
        beginSqlExprEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getBeginExpr());
        endSqlExprEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getEndExpr());
    }

    public SQLBetweenEvaluator(){

    }


    public SqlValue eval(SqlExecutionContext sqlExecutionContext, Object data) throws SQLException {
        Comparable<Object> testValue = (Comparable<Object>) testSqlExprEvaluator.eval(sqlExecutionContext, data);
        Comparable<Object> beginValue = (Comparable<Object>) beginSqlExprEvaluator.eval(sqlExecutionContext, data);
        Comparable<Object> endValue = (Comparable<Object>) endSqlExprEvaluator.eval(sqlExecutionContext, data);
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
