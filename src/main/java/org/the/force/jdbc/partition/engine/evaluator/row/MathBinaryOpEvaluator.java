package org.the.force.jdbc.partition.engine.evaluator.row;

import com.google.common.collect.Lists;
import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.value.SqlNull;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/15.
 */
public class MathBinaryOpEvaluator extends AbstractSqlExprEvaluator {

    private  SqlExprEvaluator left;
    private  SqlExprEvaluator right;
    private  SQLBinaryOperator operator;

    public MathBinaryOpEvaluator(LogicDbConfig logicDbConfig, SQLBinaryOpExpr originalSqlExpr) {
        super(originalSqlExpr);
        left = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getLeft());
        right = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getRight());
        this.operator = originalSqlExpr.getOperator();
    }

    public MathBinaryOpEvaluator(){

    }

    public SqlValue eval(SqlLineExecRequest sqlLineExecRequest, Object rows) throws SQLException {
        SqlValue leftValue = (SqlValue) this.left.eval(sqlLineExecRequest, rows);
        SqlValue rightValue = (SqlValue) this.right.eval(sqlLineExecRequest, rows);
        if (leftValue instanceof SqlNull || rightValue instanceof SqlNull) {
            throw new RuntimeException("SqlNull 不能用于算术运算");
        }
        if (operator == SQLBinaryOperator.Add) {
            return leftValue.add(rightValue);
        } else if (operator == SQLBinaryOperator.Subtract) {
            return leftValue.subtract(rightValue);
        } else if (operator == SQLBinaryOperator.Multiply) {
            return leftValue.multiply(rightValue);
        } else if (operator == SQLBinaryOperator.Divide) {
            return leftValue.divide(rightValue);
        } else if (operator == SQLBinaryOperator.Mod) {
            return leftValue.mod(rightValue);
        }
        throw new UnsupportedOperationException(SQLUtils.toMySqlString(getOriginalSqlExpr()));
    }

    public List<SqlExprEvaluator> children() {
        return Lists.newArrayList(left,right);
    }
}
