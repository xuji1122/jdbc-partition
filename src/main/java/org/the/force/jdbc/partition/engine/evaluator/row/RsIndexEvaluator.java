package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/22.
 */
public class RsIndexEvaluator extends AbstractSqlExprEvaluator {
    /**
     * 直接取值所需的两个字段
     */
    private final int index;

    private final String label;

    public RsIndexEvaluator(SQLExpr originalSqlExpr, int index, String label) {
        super(originalSqlExpr);
        this.index = index;
        this.label = label;
    }

    public Object eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        return null;
    }

    public int getIndex() {
        return index;
    }

    public String getLabel() {
        return label;
    }
}
