package org.the.force.jdbc.partition.engine.evaluator.row;

import org.the.force.jdbc.partition.engine.evaluator.AbstractSqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/15.
 * 标识类
 * 标识解析的是sql问指定的value sql直接量,不包括sql的列引用代表的值
 */
public abstract class SqlLiteralEvaluator extends AbstractSqlExprEvaluator {

    public SqlLiteralEvaluator(SQLExpr originalSqlExpr) {
        super(originalSqlExpr);
    }

    public final SqlValue eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        return eval();
    }

    public abstract SqlValue eval() throws SQLException;

    public List<SqlExprEvaluator> children() {
        return new ArrayList<>(0);
    }
}
