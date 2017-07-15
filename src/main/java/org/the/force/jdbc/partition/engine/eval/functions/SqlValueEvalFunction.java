package org.the.force.jdbc.partition.engine.eval.functions;

import org.the.force.jdbc.partition.engine.eval.SqlExprEvalFunction;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

/**
 * Created by xuji on 2017/7/15.
 */
public abstract class SqlValueEvalFunction extends SqlExprEvalFunction {

    public SqlValueEvalFunction(SQLExpr originalSqlExpr) {
        super(originalSqlExpr);
    }

}
