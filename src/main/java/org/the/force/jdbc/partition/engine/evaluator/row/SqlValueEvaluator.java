package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/15.
 * 标识类
 * 标识解析的是sql问指定的value sql直接量或者sqlParameter ，不包括sql的列引用代表的值
 */
public abstract class SqlValueEvaluator extends AbstractSqlExprEvaluator {

    public SqlValueEvaluator(SQLExpr originalSqlExpr) {
        super(originalSqlExpr);
    }

    public final Object eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        return eval(sqlExprEvalContext.getLogicSqlParameterHolder());
    }

    public abstract Object eval(LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException;

}
