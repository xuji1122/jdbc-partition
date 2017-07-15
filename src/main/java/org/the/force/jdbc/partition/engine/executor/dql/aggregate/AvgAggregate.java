package org.the.force.jdbc.partition.engine.executor.dql.aggregate;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.eval.functions.AggregateFunction;
import org.the.force.jdbc.partition.engine.executor.eval.SqlValueEvalContext;
import org.the.force.jdbc.partition.engine.result.DataItemRow;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/13.
 */
public class AvgAggregate extends AggregateFunction {


    public AvgAggregate(SQLAggregateExpr sqlExpr) {
        super(sqlExpr);
    }

    public Object getValue(SqlValueEvalContext sqlValueEvalContext, LogicSqlParameterHolder logicSqlParameterHolder, Object rows) throws SQLException {
        return null;
    }


}
