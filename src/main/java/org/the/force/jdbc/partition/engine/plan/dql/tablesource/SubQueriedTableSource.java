package org.the.force.jdbc.partition.engine.plan.dql.tablesource;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.SQLHint;
import org.druid.sql.ast.SQLObjectImpl;
import org.druid.sql.ast.statement.SQLSelectQuery;
import org.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.druid.sql.ast.statement.SQLTableSource;
import org.druid.sql.ast.statement.SQLTableSourceImpl;
import org.druid.sql.ast.statement.SQLUnionQuery;
import org.druid.sql.visitor.SQLASTVisitor;
import org.the.force.jdbc.partition.engine.plan.QueryPlan;
import org.the.force.jdbc.partition.engine.plan.dql.unionquery.StatementUnionQueryPlan;
import org.the.force.jdbc.partition.engine.plan.dql.blockquery.StatementBlockQueryPlan;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.util.List;

/**
 * Created by xuji on 2017/6/4.
 */
public class SubQueriedTableSource extends SQLTableSourceImpl implements SQLTableSource {
    private final LogicDbConfig logicDbConfig;
    private final SQLSubqueryTableSource subQueryTableSource;
    private final SQLExpr originalWhere;
    private QueryPlan queryEngine;



    private SQLExpr newWhere;

    public SubQueriedTableSource(LogicDbConfig logicDbConfig, SQLSubqueryTableSource subQueryTableSource, SQLExpr originalWhere) throws Exception {
        this.logicDbConfig = logicDbConfig;
        this.subQueryTableSource = subQueryTableSource;
        this.originalWhere = originalWhere;
        SQLSelectQuery sqlSelectQuery = subQueryTableSource.getSelect().getQuery();
        if (sqlSelectQuery == null) {
            //TODO
            return;
        }
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            queryEngine = new StatementBlockQueryPlan(logicDbConfig, (SQLSelectQueryBlock) sqlSelectQuery);
        } else if (sqlSelectQuery instanceof SQLUnionQuery) {
            queryEngine = new StatementUnionQueryPlan(logicDbConfig, (SQLUnionQuery) sqlSelectQuery);
        } else {

        }
    }

    protected void accept0(SQLASTVisitor visitor) {

    }

    public String getAlias() {
        return subQueryTableSource.getAlias();
    }

    public void setAlias(String alias) {
        subQueryTableSource.setAlias(alias);
    }

    public List<SQLHint> getHints() {
        return subQueryTableSource.getHints();
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public SQLSubqueryTableSource getSubQueryTableSource() {
        return subQueryTableSource;
    }

    public QueryPlan getQueryEngine() {
        return queryEngine;
    }

    public SQLExpr getOriginalWhere() {
        return originalWhere;
    }

    public SQLExpr getNewWhere() {
        return newWhere;
    }
}
