package org.the.force.jdbc.partition.engine.evaluator.subqueryexpr;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.evaluator.ExprGatherConfig;
import org.the.force.jdbc.partition.engine.executor.SqlExecutionContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.factory.BlockQueryExecutorFactory;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLQueryExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelect;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/6/3.
 * 作为叶子节点
 */
public class SqlQueryExpr extends SQLQueryExpr implements SqlExprEvaluator {

    private final LogicDbConfig logicDbConfig;

    private final SQLQueryExpr sqlQueryExpr;

    private final QueryExecutor queryExecutor;

    public SqlQueryExpr(LogicDbConfig logicDbConfig, SQLQueryExpr sqlQueryExpr) {
        this.logicDbConfig = logicDbConfig;
        this.sqlQueryExpr = sqlQueryExpr;
        super.setSubQuery(sqlQueryExpr.getSubQuery());
        super.setParent(sqlQueryExpr.getParent());
        attributes = sqlQueryExpr.getAttributesDirect();
        SQLSelect sqlSelect = sqlQueryExpr.getSubQuery();
        SQLSelectQuery sqlSelectQuery = sqlSelect.getQuery();
        if (sqlSelectQuery == null) {
            throw new ParserException("sqlSelect.getQuery()==null");
        }
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            this.queryExecutor = new BlockQueryExecutorFactory(logicDbConfig, (SQLSelectQueryBlock) sqlSelectQuery).buildQueryExecutor();
        } else if (sqlSelectQuery instanceof SQLUnionQuery) {
            throw new ParserException("SQLUnionQuery 不支持" + sqlSelectQuery.getClass());
        } else {
            throw new ParserException("不受支持的sqlSelectQuery类型" + sqlSelectQuery.getClass());
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PartitionSqlASTVisitor) {
            PartitionSqlASTVisitor partitionSqlASTVisitor = (PartitionSqlASTVisitor) visitor;
            if (partitionSqlASTVisitor.visit(this)) {
                queryExecutor.accept(visitor);
            }
        } else {
            SQLQueryExpr sqlQueryExpr = this;
            if (visitor.visit(sqlQueryExpr)) {
                queryExecutor.getStatement().accept(visitor);
            }
            visitor.endVisit(sqlQueryExpr);
        }
    }

    public QueryExecutor getQueryExecutor() {
        return queryExecutor;
    }

    public SQLQueryExpr getSqlQueryExpr() {
        return sqlQueryExpr;
    }


    public Object eval(SqlExecutionContext sqlExecutionContext, Object data) throws SQLException {
        return null;
    }

    public SQLExpr getOriginalSqlExpr() {
        return sqlQueryExpr;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        return sqlQueryExpr.equals(o);
    }

    public int hashCode() {

        return sqlQueryExpr.hashCode();

    }

    public String toString() {
        return PartitionSqlUtils.toSql(sqlQueryExpr, logicDbConfig.getSqlDialect());
    }


    public <T extends SqlExprEvaluator> void gatherExprEvaluator(Class<T> target, ExprGatherConfig exprGatherConfig, List<T> resultList) {
        if (exprGatherConfig.isChildClassMatch()) {
            if (target.isAssignableFrom(SqlQueryExpr.class)) {
                resultList.add((T) this);
            }
        } else {
            if (SqlQueryExpr.class == target) {
                resultList.add((T) this);
            }
        }
    }

    public List<SqlExprEvaluator> children() {
        return new ArrayList<>(0);
    }

    public void setFromSQLExpr(SQLExpr fromSQLExpr) {

    }
}
