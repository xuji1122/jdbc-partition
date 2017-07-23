package org.the.force.jdbc.partition.engine.evaluator.row;

import com.google.common.collect.Lists;
import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.value.SqlNull;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.literal.TextValue;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/21.
 */
public class SQLConcatEvaluator extends AbstractSqlExprEvaluator {
    private final SqlExprEvaluator left;
    private final SqlExprEvaluator right;

    public SQLConcatEvaluator(LogicDbConfig logicDbConfig, SQLBinaryOpExpr originalSqlExpr) {
        super(originalSqlExpr);
        left = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getLeft());
        right = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getRight());
    }

    public SqlValue eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        Object leftValue = this.left.eval(sqlExprEvalContext, data);
        Object rightValue = this.right.eval(sqlExprEvalContext, data);
        if (leftValue instanceof SqlNull || rightValue instanceof SqlNull) {
            throw new SqlParseException("null || null is invalid");
        }
        return new TextValue(leftValue.toString() + rightValue.toString());
    }

    public List<SqlExprEvaluator> children() {
        return Lists.newArrayList(left, right);
    }
}
