package org.the.force.jdbc.partition.engine.evaluator.row;

import com.google.common.collect.Lists;
import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.types.BooleanValue;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/15.
 */
public class SQLEqualEvaluator extends AbstractSqlExprEvaluator {

    private SqlExprEvaluator leftEvaluator;
    private SqlExprEvaluator rightEvaluator;

    public SQLEqualEvaluator(LogicDbConfig logicDbConfig, SQLBinaryOpExpr originalSqlExpr) {
        super(originalSqlExpr);
        leftEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getLeft());
        rightEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getRight());
    }

    public SqlValue eval(SqlLineExecRequest sqlLineExecRequest, Object rows) throws SQLException {
        Object leftValue = this.leftEvaluator.eval(sqlLineExecRequest, rows);
        Object rightValue = this.rightEvaluator.eval(sqlLineExecRequest, rows);
        if (leftValue == null || rightValue == null) {
            return new BooleanValue(false);
        }
        return new BooleanValue(leftValue.equals(rightValue));
    }

    public SQLEqualEvaluator() {

    }

    public SqlExprEvaluator getLeftEvaluator() {
        return leftEvaluator;
    }

    public SqlExprEvaluator getRightEvaluator() {
        return rightEvaluator;
    }

    public List<SqlExprEvaluator> children() {
        return Lists.newArrayList(leftEvaluator, rightEvaluator);
    }

}
