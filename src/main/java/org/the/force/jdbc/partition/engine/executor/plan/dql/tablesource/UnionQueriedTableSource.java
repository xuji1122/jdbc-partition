package org.the.force.jdbc.partition.engine.executor.plan.dql.tablesource;

import org.the.force.jdbc.partition.engine.executor.plan.dql.PlanedTableSource;
import org.the.force.jdbc.partition.engine.executor.plan.dql.UnionQueryPlan;
import org.the.force.jdbc.partition.engine.executor.plan.dql.unionquery.StatementUnionQueryPlan;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.List;

/**
 * Created by xuji on 2017/7/1.
 */
public class UnionQueriedTableSource extends PlanedTableSource {

    private final SQLUnionQueryTableSource sqlUnionQueryTableSource;

    private final SqlTable sqlTable;
    private final SQLExpr outerCondition;

    private final UnionQueryPlan unionQueryPlan;

    public UnionQueriedTableSource(LogicDbConfig logicDbConfig, SQLUnionQueryTableSource sqlUnionQueryTableSource, SqlTable sqlTable, SQLExpr outerCondition) {
        super(logicDbConfig);
        this.sqlUnionQueryTableSource = sqlUnionQueryTableSource;
        super.setParent(sqlUnionQueryTableSource.getParent());
        this.sqlTable = sqlTable;
        this.outerCondition = outerCondition;
        unionQueryPlan = new StatementUnionQueryPlan(logicDbConfig, sqlUnionQueryTableSource.getUnion(), outerCondition);
    }

    protected void accept0(SQLASTVisitor visitor) {

    }

    public String getAlias() {
        return null;
    }

    public void setAlias(String alias) {

    }

    public List<SQLHint> getHints() {
        return null;
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public SQLUnionQueryTableSource getSqlUnionQueryTableSource() {
        return sqlUnionQueryTableSource;
    }

    public SqlTable getSqlTable() {
        return sqlTable;
    }

    public SQLExpr getOuterCondition() {
        return outerCondition;
    }
}
