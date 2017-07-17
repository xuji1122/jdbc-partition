package org.the.force.jdbc.partition.engine.executor.dql.tablesource;

import org.the.force.jdbc.partition.engine.executor.dql.ExecutableTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.filter.QueryReferFilter;
import org.the.force.jdbc.partition.engine.parser.elements.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTableRefers;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by xuji on 2017/7/6.
 */
public class AtomicTableSource extends SQLExprTableSource implements ExecutableTableSource {
    private final LogicDbConfig logicDbConfig;
    private final SQLExprTableSource sqlExprTableSource;
    private final QueryReferFilter queryReferFilter;
    private final ConditionalSqlTable sqlTable;
    private final SqlTableRefers sqlTableRefers;

    public AtomicTableSource(LogicDbConfig logicDbConfig, QueryReferFilter queryReferFilter,SqlTableRefers sqlTableRefers) {
        this.logicDbConfig = logicDbConfig;
        this.sqlExprTableSource = (SQLExprTableSource)queryReferFilter.getReferTable().getSQLTableSource();
        this.sqlTableRefers = sqlTableRefers;
        super.alias = sqlExprTableSource.getAlias();
        super.hints = sqlExprTableSource.getHints();
        super.setFlashback(sqlExprTableSource.getFlashback());
        super.setExpr(sqlExprTableSource.getExpr());
        super.getPartitions().addAll(sqlExprTableSource.getPartitions());
        super.setSchemaObject(sqlExprTableSource.getSchemaObject());
        this.sqlTable = queryReferFilter.getReferTable();
        this.queryReferFilter = queryReferFilter;
    }


    public SQLExprTableSource getSqlExprTableSource() {
        return sqlExprTableSource;
    }

    public ConditionalSqlTable getSqlTable() {
        return sqlTable;
    }


    public SqlTableRefers getSqlTableRefers() {
        return sqlTableRefers;
    }

    public QueryReferFilter getQueryReferFilter() {
        return queryReferFilter;
    }

    protected void accept0(SQLASTVisitor visitor) {
        sqlExprTableSource.accept(visitor);
    }


}
