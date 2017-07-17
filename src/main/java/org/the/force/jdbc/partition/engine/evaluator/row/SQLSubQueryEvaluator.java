package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SubQueriedExpr;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/16.
 */
public class SQLSubQueryEvaluator extends AbstractSqlExprEvaluator {

    private SqlExprEvaluator sqlExprEvaluator;

    public SQLSubQueryEvaluator(LogicDbConfig logicDbConfig, SubQueriedExpr originalSqlExpr) {
        super(originalSqlExpr);
        sqlExprEvaluator = originalSqlExpr;
    }

    public Object eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        return sqlExprEvaluator.eval(sqlExprEvalContext, data);
    }


}
