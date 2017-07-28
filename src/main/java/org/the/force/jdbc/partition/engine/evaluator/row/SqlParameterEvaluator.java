package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.executor.SqlExecutionContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLVariantRefExpr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/20.
 */
public class SqlParameterEvaluator extends AbstractSqlExprEvaluator {

    private int index;

    public SqlParameterEvaluator(SQLVariantRefExpr sqlVariantRefExpr) {
        super(sqlVariantRefExpr);
        this.index = sqlVariantRefExpr.getIndex();
    }

    public SqlParameterEvaluator() {
    }

    public SqlParameter eval(SqlExecutionContext sqlExecutionContext, Object data) throws SQLException {
        return sqlExecutionContext.getLogicSqlParameterHolder().getSqlParameter(index);
    }

    public List<SqlExprEvaluator> children() {
        return new ArrayList<>(0);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
