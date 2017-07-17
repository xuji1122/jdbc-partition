package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLCaseExpr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/16.
 */
public class SQLCaseEvaluator extends AbstractSqlExprEvaluator {

    private final SqlExprEvaluator valueEvaluator;

    private final SqlExprEvaluator elseEvaluator;

    private final List<SqlExprEvaluator[]> itemsEvaluator = new ArrayList<>();

    public SQLCaseEvaluator(LogicDbConfig logicDbConfig, SQLCaseExpr sqlCaseExpr) {
        super(sqlCaseExpr);
        valueEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(sqlCaseExpr.getValueExpr());
        if (sqlCaseExpr.getElseExpr() == null) {
            elseEvaluator = null;
        } else {
            elseEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(sqlCaseExpr.getElseExpr());
        }
        List<SQLCaseExpr.Item> items = sqlCaseExpr.getItems();
        for (int i = 0; i < items.size(); i++) {
            SQLCaseExpr.Item item = items.get(i);
            SqlExprEvaluator[] pair = new SqlExprEvaluator[2];
            pair[0] = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(item.getConditionExpr());
            pair[1] = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(item.getValueExpr());
            itemsEvaluator.add(pair);
        }
    }

    public Object eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        Object value = valueEvaluator.eval(sqlExprEvalContext, data);
        if (value == null) {
            return false;
        }
        for (SqlExprEvaluator[] sqlExprEvaluators : itemsEvaluator) {
            Object condition = sqlExprEvaluators[0].eval(sqlExprEvalContext, data);
            if (value.equals(condition)) {
                return sqlExprEvaluators[1].eval(sqlExprEvalContext, data);
            }
        }
        if (elseEvaluator != null) {
            return elseEvaluator.eval(sqlExprEvalContext, data);
        }
        return null;
    }
}
