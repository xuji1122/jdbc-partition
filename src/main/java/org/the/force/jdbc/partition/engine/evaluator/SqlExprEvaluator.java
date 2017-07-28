package org.the.force.jdbc.partition.engine.evaluator;

import org.the.force.jdbc.partition.engine.executor.SqlExecutionContext;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/7/13.
 * SQLExpr的求值程序
 * 可以被当做普通的SQLExpr被处理
 */
public interface SqlExprEvaluator extends SQLExpr {

    Object eval(SqlExecutionContext sqlExecutionContext, Object data) throws SQLException;

    SQLExpr getOriginalSqlExpr();

    void setFromSQLExpr(SQLExpr fromSQLExpr);


    List<SqlExprEvaluator> children();

    /**
     * 收集求值处理过程中指定类型的表达式
     * @param target
     * @param exprGatherConfig   Expr遍历搜集的相关条件设置
     * @param resultList    保存结果集的对象
     * @return
     */
    <T extends SqlExprEvaluator> void gatherExprEvaluator(Class<T> target, ExprGatherConfig exprGatherConfig, List<T> resultList);

}
