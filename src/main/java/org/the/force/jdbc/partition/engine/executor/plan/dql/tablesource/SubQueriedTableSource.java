package org.the.force.jdbc.partition.engine.executor.plan.dql.tablesource;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.executor.plan.QueryPlan;
import org.the.force.jdbc.partition.engine.executor.plan.dql.PlanedTableSource;
import org.the.force.jdbc.partition.engine.executor.plan.dql.blockquery.StatementBlockQueryPlan;
import org.the.force.jdbc.partition.engine.executor.plan.dql.unionquery.StatementUnionQueryPlan;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by xuji on 2017/6/4.
 */
public class SubQueriedTableSource extends PlanedTableSource {
    private final SQLSubqueryTableSource subQueryTableSource;
    private final SqlTable sqlTable;
    private final SQLExpr outerCondition;
    private final QueryPlan queryEngine;

    public SubQueriedTableSource(LogicDbConfig logicDbConfig, SQLSubqueryTableSource subQueryTableSource, SqlTable sqlTable, SQLExpr outerCondition){
        super(logicDbConfig);
        this.subQueryTableSource = subQueryTableSource;
        super.setParent(subQueryTableSource.getParent());
        this.sqlTable = sqlTable;
        this.outerCondition = outerCondition;
        SQLSelectQuery sqlSelectQuery = subQueryTableSource.getSelect().getQuery();
        if (sqlSelectQuery == null) {
            throw new ParserException("sqlSelectQuery == null");
        }

        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            queryEngine = new StatementBlockQueryPlan(logicDbConfig, (SQLSelectQueryBlock) sqlSelectQuery,outerCondition);
        } else if (sqlSelectQuery instanceof SQLUnionQuery) {
            queryEngine = new StatementUnionQueryPlan(logicDbConfig, (SQLUnionQuery) sqlSelectQuery,outerCondition);
        } else {
            //TODO
            throw new ParserException("un supported sql elements:" + PartitionSqlUtils.toSql(sqlSelectQuery, logicDbConfig.getSqlDialect()));
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


    public SqlTable getSqlTable() {
        return sqlTable;
    }

    public SQLExpr getOuterCondition() {
        return outerCondition;
    }
}
