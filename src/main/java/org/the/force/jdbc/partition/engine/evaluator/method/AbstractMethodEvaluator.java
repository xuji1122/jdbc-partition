package org.the.force.jdbc.partition.engine.evaluator.method;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLMethodInvokeExpr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/16.
 */
public abstract class AbstractMethodEvaluator extends AbstractSqlExprEvaluator {

    private List<SqlExprEvaluator> argumentEvaluators = new ArrayList<>();

    public AbstractMethodEvaluator(LogicDbConfig logicDbConfig, SQLMethodInvokeExpr originalSqlExpr) {
        super(originalSqlExpr);
        List<SQLExpr> arguments = originalSqlExpr.getParameters();
        for (SQLExpr sqlExpr : arguments) {
            SqlExprEvaluator sqlExprEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(sqlExpr);
            argumentEvaluators.add(sqlExprEvaluator);
        }
    }

    public AbstractMethodEvaluator(){

    }


    protected List<Object> evalArguments(SqlLineExecRequest sqlLineExecRequest, Object data) throws SQLException {
        List<Object> argumentValues = new ArrayList<>();
        for (SqlExprEvaluator sqlExprEvaluator : argumentEvaluators) {
            Object value = sqlExprEvaluator.eval(sqlLineExecRequest, data);
            argumentValues.add(value);
        }
        return argumentValues;
    }

    public List<SqlExprEvaluator> children() {
        List<SqlExprEvaluator> list  = new ArrayList<>();
        list.addAll(argumentEvaluators);
        return list;
    }
}
