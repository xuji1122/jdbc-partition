package org.the.force.jdbc.partition.engine.executor.plan.dql.subqueryexpr;

import org.the.force.jdbc.partition.engine.executor.plan.QueryPlan;
import org.the.force.jdbc.partition.engine.executor.plan.dql.blockquery.StatementBlockQueryPlan;
import org.the.force.jdbc.partition.engine.executor.plan.dql.unionquery.StatementUnionQueryPlan;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInSubQueryExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelect;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/6/4.
 * 叶子节点
 */
public class SQLInSubQueriedExpr extends SQLInListExpr implements SQLExpr {
    private final LogicDbConfig logicDbConfig;
    private final SQLInSubQueryExpr sqlInSubQueryExpr;
    private final QueryPlan queryPlan;

    public SQLInSubQueriedExpr(LogicDbConfig logicDbConfig, SQLInSubQueryExpr sqlInSubQueryExpr) {
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
            this.queryPlan = new StatementBlockQueryPlan(logicDbConfig, (SQLSelectQueryBlock) sqlSelectQuery, null);
        } else if (sqlSelectQuery instanceof SQLUnionQuery) {
            this.queryPlan = new StatementUnionQueryPlan(logicDbConfig, (SQLUnionQuery) sqlSelectQuery, null);
        } else {
            throw new ParserException("不受支持的sqlSelectQuery类型" + sqlSelectQuery.getClass());
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PartitionSqlASTVisitor) {
            PartitionSqlASTVisitor partitionSqlASTVisitor = (PartitionSqlASTVisitor) visitor;
            partitionSqlASTVisitor.visit(this);
        } else {
            visitor.visit(sqlInSubQueryExpr);
        }
    }

    public SQLInSubQueryExpr getSqlInSubQueryExpr() {
        return sqlInSubQueryExpr;
    }


    public boolean equals(Object o) {
        if (o instanceof SQLInSubQueriedExpr) {
            return sqlInSubQueryExpr.equals(((SQLInSubQueriedExpr) o).getSqlInSubQueryExpr());
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
}
