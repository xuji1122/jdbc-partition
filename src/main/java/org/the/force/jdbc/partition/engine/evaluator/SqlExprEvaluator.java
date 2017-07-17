package org.the.force.jdbc.partition.engine.evaluator;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/13.
 * SQLExpr的求值程序
 * 可以被当做普通的SQLExpr被处理
 */
public interface SqlExprEvaluator extends SQLExpr {

    Object eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException;

    SQLExpr getOriginalSqlExpr();

}
