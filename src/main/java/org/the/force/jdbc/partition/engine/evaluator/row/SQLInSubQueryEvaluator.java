package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SqlInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.value.types.BooleanValue;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/16.
 */
public class SQLInSubQueryEvaluator extends SQLInListEvaluator {

    private SqlInSubQueriedExpr valuesEvaluator;


    public SQLInSubQueryEvaluator(LogicDbConfig logicDbConfig, SqlInSubQueriedExpr originalSqlExpr) {
        super(logicDbConfig, originalSqlExpr);
        this.valuesEvaluator = originalSqlExpr;
    }

    public SQLInSubQueryEvaluator() {

    }


    public BooleanValue eval(SqlLineExecRequest sqlLineExecRequest, Object data) throws SQLException {

        return null;
    }

    public List<Object[]> getTargetListValue(SqlLineExecRequest sqlLineExecRequest, Object data) throws SQLException {
        return valuesEvaluator.eval(sqlLineExecRequest, data);
    }

    public List<SqlExprEvaluator> children() {
        List<SqlExprEvaluator> list = new ArrayList<>();
        list.add(exprEvaluator);
        list.add(valuesEvaluator);
        return list;
    }


}
