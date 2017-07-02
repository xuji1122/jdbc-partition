package org.the.force.jdbc.partition.engine.plan.dql.blockquery;

import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlNameParser;
import org.the.force.jdbc.partition.engine.plan.dql.BlockQueryPlan;
import org.the.force.jdbc.partition.engine.plan.dql.tablesource.JoinedTableSource;
import org.the.force.jdbc.partition.engine.plan.dql.tablesource.SubQueriedTableSource;
import org.the.force.jdbc.partition.engine.plan.dql.tablesource.UnionQueriedTableSource;
import org.the.force.jdbc.partition.engine.plan.model.SqlExprTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

/**
 * Created by xuji on 2017/6/3.
 */
public class StatementBlockQueryPlan extends BlockQueryPlan {

    private final SQLSelectQueryBlock queryBlock;

    private SQLTableSource sqlTableSource;

    private SqlExprTable sqlTable;

    public StatementBlockQueryPlan(LogicDbConfig logicDbConfig, SQLSelectQueryBlock selectQuery) throws Exception {
        super(logicDbConfig);
        this.queryBlock = selectQuery;
        SQLTableSource from = selectQuery.getFrom();
        if (from instanceof SQLExprTableSource) {
            SQLExprTableSource sqlExprTableSource = (SQLExprTableSource) sqlTableSource;
            this.sqlTable = SqlNameParser.getSQLExprTable(sqlExprTableSource);
            this.sqlTableSource = sqlExprTableSource;
        } else if (from instanceof SQLJoinTableSource) {
            this.sqlTableSource = new JoinedTableSource(logicDbConfig, (SQLJoinTableSource) from, selectQuery.getWhere());
        } else if (from instanceof SQLSubqueryTableSource) {
            this.sqlTableSource = new SubQueriedTableSource(logicDbConfig, (SQLSubqueryTableSource) from, selectQuery.getWhere());
        } else if (from instanceof SQLUnionQueryTableSource) {
            this.sqlTableSource = new UnionQueriedTableSource(logicDbConfig, (SQLUnionQueryTableSource) from, selectQuery.getWhere());
        } else {
            //TODO
        }
    }



    public SQLSelectQueryBlock getQueryBlock() {
        return queryBlock;
    }

    public SQLTableSource getSqlTableSource() {
        return sqlTableSource;
    }

    public SqlExprTable getSqlTable() {
        return sqlTable;
    }

}
