package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/16.
 */
public class UnKnowEvaluator extends AbstractSqlExprEvaluator {

    private final LogicDbConfig logicDbConfig;

    public UnKnowEvaluator(LogicDbConfig logicDbConfig, SQLExpr originalSqlExpr) {
        super(originalSqlExpr);
        this.logicDbConfig = logicDbConfig;
    }

    public Object eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        throw new SqlParseException("无法识别evalFunction " + PartitionSqlUtils.toSql(getOriginalSqlExpr(), logicDbConfig.getSqlDialect()));
    }
}
