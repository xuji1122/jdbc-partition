package org.the.force.jdbc.partition.engine.evaluator.row;

import com.google.common.collect.Lists;
import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.executor.SqlExecutionContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.value.SqlNull;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.types.BooleanValue;
import org.the.force.jdbc.partition.exception.PartitionSystemException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/15.
 */
public class RelationalBinaryOpEvaluator extends AbstractSqlExprEvaluator {

    private  SqlExprEvaluator left;
    private  SqlExprEvaluator right;
    private  SQLBinaryOperator operator;

    public RelationalBinaryOpEvaluator(LogicDbConfig logicDbConfig, SQLBinaryOpExpr originalSqlExpr) {
        super(originalSqlExpr);
        left = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getLeft());
        right = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getRight());
        this.operator = originalSqlExpr.getOperator();
    }

    public RelationalBinaryOpEvaluator(){

    }

    public BooleanValue eval(SqlExecutionContext sqlExecutionContext, Object rows) throws SQLException {
        SqlValue leftValue = (SqlValue)this.left.eval(sqlExecutionContext, rows);
        Object rightValue = (SqlValue)this.right.eval(sqlExecutionContext, rows);
        if (leftValue == null || rightValue == null) {
            return new BooleanValue(false);
        }
        if (operator == SQLBinaryOperator.Is) {
            return new BooleanValue((rightValue instanceof SqlNull));
        }
        if (operator == SQLBinaryOperator.IsNot) {
            return new BooleanValue(!(rightValue instanceof SqlNull));
        }
        if (leftValue instanceof SqlNull || rightValue instanceof SqlNull) {
            return new BooleanValue(false);
        }
        if (operator == SQLBinaryOperator.NotEqual) {
            return new BooleanValue(!leftValue.equals(rightValue));
        }
        if (!(leftValue instanceof Comparable<?>) || !(rightValue instanceof Comparable<?>)) {
            throw new PartitionSystemException("!(leftValue instanceof Comparable<?>) || !(rightValue instanceof Comparable<?>)");
        }
        Comparable<Object> c1 = (Comparable<Object>) leftValue;
        Comparable<Object> c2 = (Comparable<Object>) rightValue;
        int result = c1.compareTo(c2);
        switch (operator) {
            case GreaterThan:
                return new BooleanValue(result > 0);
            case GreaterThanOrEqual:
                return new BooleanValue(result >= 0);
            case LessThan:
                return new BooleanValue(result < 0);
            case LessThanOrEqual:
                return new BooleanValue(result <= 0);
            //TODO 各种条件判断  like not like
            default:
                throw new PartitionSystemException("not expected operator");
        }
    }

    public List<SqlExprEvaluator> children() {
        return Lists.newArrayList(left,right);
    }
}
