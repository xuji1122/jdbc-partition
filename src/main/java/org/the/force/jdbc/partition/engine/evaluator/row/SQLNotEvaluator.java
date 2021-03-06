package org.the.force.jdbc.partition.engine.evaluator.row;

import com.google.common.collect.Lists;
import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.value.types.BooleanValue;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNotExpr;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/16.
 */
public class SQLNotEvaluator extends AbstractSqlExprEvaluator {

    private SqlExprEvaluator sqlExprEvaluator;

    public SQLNotEvaluator(LogicDbConfig logicDbConfig, SQLNotExpr originalSqlExpr) {
        super(originalSqlExpr);
        sqlExprEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(originalSqlExpr.getExpr());

    }

    public SQLNotEvaluator(){

    }

    public BooleanValue eval(SqlLineExecRequest sqlLineExecRequest, Object data) throws SQLException {
        return new BooleanValue(!((BooleanValue) sqlExprEvaluator.eval(sqlLineExecRequest, data)).getValue());
    }


    public List<SqlExprEvaluator> children() {
        return Lists.newArrayList(sqlExprEvaluator);
    }
}
