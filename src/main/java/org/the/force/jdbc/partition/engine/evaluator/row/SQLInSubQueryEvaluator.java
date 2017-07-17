package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SQLInSubQueriedExpr;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/16.
 */
public class SQLInSubQueryEvaluator extends SQLInListEvaluator {

    private final SQLInSubQueriedExpr valuesEvaluator;

    public SQLInSubQueryEvaluator(LogicDbConfig logicDbConfig, SQLInSubQueriedExpr originalSqlExpr) {
        super(logicDbConfig, originalSqlExpr);
        this.valuesEvaluator = originalSqlExpr;
    }

    public Boolean eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {

        return null;
    }

    public List<Object[]> getTargetListValue(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        return valuesEvaluator.eval(sqlExprEvalContext, data);
    }


}
