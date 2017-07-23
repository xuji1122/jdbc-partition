package org.the.force.jdbc.partition.engine.evaluator.factory;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLBetweenEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLCaseEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInSubQueryEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLListEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLNotEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.UnKnowEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SqlInSubQueriedExpr;
import org.the.force.jdbc.partition.engine.evaluator.subqueryexpr.SqlQueryExpr;
import org.the.force.jdbc.partition.engine.sql.SqlRefer;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAggregateExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBetweenExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLCaseExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInSubQueryExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLMethodInvokeExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLNotExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLQueryExpr;

/**
 * Created by xuji on 2017/1/2.
 */
public class SqlExprEvaluatorFactory {


    private final LogicDbConfig logicDbConfig;

    private final SqlValueEvaluatorFactory sqlValueEvaluatorFactory;

    private final AggregateEvaluatorFactory aggregateEvaluatorFactory;

    private final BinaryOpEvaluatorFactory binaryOpEvaluatorFactory;

    private final SqlMethodEvaluatorFactory sqlMethodEvaluatorFactory;


    public SqlExprEvaluatorFactory(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
        sqlValueEvaluatorFactory = new SqlValueEvaluatorFactory();
        aggregateEvaluatorFactory = new AggregateEvaluatorFactory(logicDbConfig);
        binaryOpEvaluatorFactory = new BinaryOpEvaluatorFactory(logicDbConfig);
        sqlMethodEvaluatorFactory = new SqlMethodEvaluatorFactory(logicDbConfig);
    }

    public SqlExprEvaluator matchSqlExprEvaluator(SQLExpr sqlExpr) {
        SqlExprEvaluator sqlExprEvaluator = sqlValueEvaluatorFactory.matchSqlExprEvaluator(sqlExpr);
        if (sqlExprEvaluator != null) {
            return sqlExprEvaluator;
        }
        if (sqlExpr instanceof SQLBinaryOpExpr) {
            return binaryOpEvaluatorFactory.matchSqlExprEvaluator((SQLBinaryOpExpr) sqlExpr);
        } else if (sqlExpr instanceof SQLName) {//SQLProperty SQLIdentifierExpr
            return new SqlRefer((SQLName) sqlExpr);
        } else if (sqlExpr instanceof SQLAggregateExpr) {
            return aggregateEvaluatorFactory.matchSqlExprEvaluator((SQLAggregateExpr) sqlExpr);
        } else if (sqlExpr instanceof SQLMethodInvokeExpr) {
            return sqlMethodEvaluatorFactory.matchSqlExprEvaluator((SQLMethodInvokeExpr) sqlExpr);
        } else if (sqlExpr instanceof SQLCaseExpr) {
            //case when
            return new SQLCaseEvaluator(logicDbConfig, (SQLCaseExpr) sqlExpr);
        } else if (sqlExpr instanceof SQLBetweenExpr) {
            return new SQLBetweenEvaluator(logicDbConfig, (SQLBetweenExpr) sqlExpr);
        } else if (sqlExpr instanceof SQLListExpr) {
            return new SQLListEvaluator(logicDbConfig, (SQLListExpr) sqlExpr);
        } else if (sqlExpr instanceof SQLNotExpr) {
            return new SQLNotEvaluator(logicDbConfig, (SQLNotExpr) sqlExpr);
        }
        // in 查询有关的
        else if (sqlExpr instanceof SqlInSubQueriedExpr) {
            return new SQLInSubQueryEvaluator(logicDbConfig, (SqlInSubQueriedExpr) sqlExpr);
        } else if (sqlExpr instanceof SQLInSubQueryExpr) {//SQLInSubQueryExpr被重置是前提
            return new SQLInSubQueryEvaluator(logicDbConfig, new SqlInSubQueriedExpr(logicDbConfig, (SQLInSubQueryExpr) sqlExpr));
        } else if (sqlExpr instanceof SQLInListExpr) {//放在子查询之后判断
            return new SQLInListEvaluator(logicDbConfig, (SQLInListExpr) sqlExpr);
        }
        //子查询
        else if (sqlExpr instanceof SqlQueryExpr) {
            return (SqlQueryExpr) sqlExpr;
            //return new SQLSubQueryEvaluator(logicDbConfig, (SqlQueryExpr) sqlExpr);
        } else if (sqlExpr instanceof SQLQueryExpr) {
            return new SqlQueryExpr(logicDbConfig, (SQLQueryExpr) sqlExpr);
        }
        return new UnKnowEvaluator(logicDbConfig, sqlExpr);
    }

}

