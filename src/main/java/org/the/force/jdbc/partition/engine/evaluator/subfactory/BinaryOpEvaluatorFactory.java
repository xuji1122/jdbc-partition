package org.the.force.jdbc.partition.engine.evaluator.subfactory;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.MathBinaryOpEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.RelationalBinaryOpEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLEqualEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.LogicBooleanEvaluator;
import org.the.force.jdbc.partition.engine.parser.ParserUtils;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;

/**
 * Created by xuji on 2017/7/16.
 */
public class BinaryOpEvaluatorFactory {

    private final LogicDbConfig logicDbConfig;

    public BinaryOpEvaluatorFactory(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    public SqlExprEvaluator matchSqlExprEvalFunction(SQLBinaryOpExpr sqlBinaryOpExpr) {
        SQLBinaryOperator operator = sqlBinaryOpExpr.getOperator();
        if (operator == SQLBinaryOperator.Equality) {
            return new SQLEqualEvaluator(logicDbConfig, sqlBinaryOpExpr);
        } else if (ParserUtils.isLogical(sqlBinaryOpExpr)) {//逻辑关系表达式
            return new LogicBooleanEvaluator(logicDbConfig, sqlBinaryOpExpr);
        } else if (ParserUtils.isRelational(operator)) {//关系表达式
            return new RelationalBinaryOpEvaluator(logicDbConfig, sqlBinaryOpExpr);
        } else {
            return new MathBinaryOpEvaluator(logicDbConfig, sqlBinaryOpExpr);
        }
    }
}
