package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.types.BooleanValue;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLCaseExpr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/16.
 */
public class SQLCaseEvaluator extends AbstractSqlExprEvaluator {

    private  SqlExprEvaluator valueEvaluator;

    private  SqlExprEvaluator elseEvaluator;

    private  List<SqlExprEvaluator[]> itemsEvaluator = new ArrayList<>();

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

    public SQLCaseEvaluator(){

    }

    public SqlValue eval(SqlLineExecRequest sqlLineExecRequest, Object data) throws SQLException {
        Object value = valueEvaluator.eval(sqlLineExecRequest, data);
        if (value == null) {
            return new BooleanValue(false);
        }
        for (SqlExprEvaluator[] sqlExprEvaluators : itemsEvaluator) {
            Object condition = sqlExprEvaluators[0].eval(sqlLineExecRequest, data);
            if (value.equals(condition)) {
                return (SqlValue) sqlExprEvaluators[1].eval(sqlLineExecRequest, data);
            }
        }
        if (elseEvaluator != null) {
            return (SqlValue) elseEvaluator.eval(sqlLineExecRequest, data);
        }
        throw new RuntimeException("case when表达式没有匹配结果");
    }

    public List<SqlExprEvaluator> children() {
        List<SqlExprEvaluator> list = new ArrayList<>();
        list.add(valueEvaluator);
        for (SqlExprEvaluator[] sqlExprEvaluators : itemsEvaluator) {
            list.add(sqlExprEvaluators[0]);
            list.add(sqlExprEvaluators[1]);
        }
        list.add(elseEvaluator);
        return list;
    }
}
