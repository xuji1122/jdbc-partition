package org.the.force.jdbc.partition.engine.evaluator.aggregate;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateOption;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/14.
 */
public abstract class AggregateEvaluator extends AbstractSqlExprEvaluator {

    private List<SqlExprEvaluator> argumentEvaluators = new ArrayList<>();

    private  boolean distinct;


    public AggregateEvaluator(LogicDbConfig logicDbConfig, SQLAggregateExpr sqlAggregateExpr) {
        super(sqlAggregateExpr);
        List<SQLExpr> arguments = sqlAggregateExpr.getArguments();
        for (SQLExpr sqlExpr : arguments) {
            SqlExprEvaluator sqlExprEvaluator = logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(sqlExpr);
            argumentEvaluators.add(sqlExprEvaluator);
        }
        distinct = sqlAggregateExpr.getOption() == SQLAggregateOption.DISTINCT;
    }

    public AggregateEvaluator(){

    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<SqlExprEvaluator> children() {
        List<SqlExprEvaluator> list  = new ArrayList<>();
        list.addAll(argumentEvaluators);
        return list;
    }

}
