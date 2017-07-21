package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLVariantRefExpr;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/20.
 */
public class SqlParameterEvaluator extends AbstractSqlExprEvaluator {

    private int index;

    public SqlParameterEvaluator(SQLVariantRefExpr sqlVariantRefExpr) {
        super(sqlVariantRefExpr);
        this.index = sqlVariantRefExpr.getIndex();
    }

    public SqlParameter eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        return sqlExprEvalContext.getLogicSqlParameterHolder().getSqlParameter(index);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
