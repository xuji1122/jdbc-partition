package org.the.force.jdbc.partition.engine.stmt.query;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;

/**
 * Created by xuji on 2017/7/22.
 */
public class ResultLimit {

    private SqlExprEvaluator offset;

    private final SqlExprEvaluator count;

    public ResultLimit(SqlExprEvaluator count) {
        this.count = count;
    }

    public SqlExprEvaluator getOffset() {
        return offset;
    }

    public void setOffset(SqlExprEvaluator offset) {
        this.offset = offset;
    }
}
