package org.the.force.jdbc.partition.engine.evaluator.subqueryexpr;

import org.the.force.jdbc.partition.engine.evaluator.ExprGatherConfig;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvalContext;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.factory.BlockQueryExecutorFactory;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInSubQueryExpr;
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
 * Created by xuji on 2017/6/4.
 * 叶子节点
 */
public class SqlInSubQueriedExpr extends SQLInSubQueryExpr implements SqlExprEvaluator {
    private final LogicDbConfig logicDbConfig;
    private final SQLInSubQueryExpr sqlInSubQueryExpr;
    private final QueryExecutor queryExecutor;

    public SqlInSubQueriedExpr(LogicDbConfig logicDbConfig, SQLInSubQueryExpr sqlInSubQueryExpr) {
        this.logicDbConfig = logicDbConfig;
        this.sqlInSubQueryExpr = sqlInSubQueryExpr;
        super.setParent(sqlInSubQueryExpr.getParent());
        super.attributes = sqlInSubQueryExpr.getAttributesDirect();
        SQLSelect sqlSelect = sqlInSubQueryExpr.getSubQuery();
        SQLSelectQuery sqlSelectQuery = sqlSelect.getQuery();
        if (sqlSelectQuery == null) {
            throw new ParserException("sqlSelect.getQuery()==null");
        }
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            this.queryExecutor = new BlockQueryExecutorFactory(logicDbConfig, (SQLSelectQueryBlock) sqlSelectQuery).buildQueryExecutor();
            this.sqlInSubQueryExpr.setSubQuery(new SQLSelect(queryExecutor.getStatement()));
        } else if (sqlSelectQuery instanceof SQLUnionQuery) {
            throw new ParserException("SQLUnionQuery 不支持" + sqlSelectQuery.getClass());
            //this.queryExecutor = new UnionQueryExecutorFactory(logicDbConfig, (SQLUnionQuery) sqlSelectQuery).buildQueryExecutor();
        } else {
            throw new ParserException("不受支持的sqlSelectQuery类型" + sqlSelectQuery.getClass());
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PartitionSqlASTVisitor) {
            PartitionSqlASTVisitor partitionSqlASTVisitor = (PartitionSqlASTVisitor) visitor;
            if (partitionSqlASTVisitor.visit(this)) {
                this.getExpr().accept(visitor);
                queryExecutor.getStatement().accept(visitor);
            }
            visitor.endVisit(this);
        } else {
            sqlInSubQueryExpr.accept(visitor);
        }
    }

    public QueryExecutor getQueryExecutor() {
        return queryExecutor;
    }

    public List<Object[]> eval(SqlExprEvalContext sqlExprEvalContext, Object data) throws SQLException {
        return null;
    }

    public SQLExpr getOriginalSqlExpr() {
        return sqlInSubQueryExpr;
    }

    public SQLInSubQueryExpr getSqlInSubQueryExpr() {
        return sqlInSubQueryExpr;
    }


    public boolean equals(Object o) {
        if (o instanceof SqlInSubQueriedExpr) {
            return sqlInSubQueryExpr.equals(((SqlInSubQueriedExpr) o).getSqlInSubQueryExpr());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return sqlInSubQueryExpr.hashCode();
    }

    public boolean isNot() {
        return sqlInSubQueryExpr.isNot();
    }

    public void setNot(boolean not) {

    }

    public SQLExpr getExpr() {
        return sqlInSubQueryExpr.getExpr();
    }

    public void setExpr(SQLExpr expr) {

    }

    public List<SQLExpr> getTargetList() {
        return new ArrayList<>();
    }

    public void setTargetList(List<SQLExpr> targetList) {

    }

    public String toString() {
        return SQLUtils.toMySqlString(sqlInSubQueryExpr);
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public <T extends SqlExprEvaluator> void gatherExprEvaluator(Class<T> target,ExprGatherConfig exprGatherConfig, List<T> resultList) {
        if (exprGatherConfig.isChildClassMatch()) {
            if (SqlInSubQueriedExpr.class.isAssignableFrom(target)) {
                resultList.add((T) this);
            }
        } else {
            if (SqlInSubQueriedExpr.class == target) {
                resultList.add((T)this);
            }
        }
    }

    public List<SqlExprEvaluator> children() {
        return new ArrayList<>(0);
    }
}
