package org.the.force.jdbc.partition.engine.executor.dql.tablesource;

import org.the.force.jdbc.partition.engine.executor.dql.ExecutableTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.filter.QueryReferFilter;
import org.the.force.jdbc.partition.engine.sql.elements.SqlTableRefers;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.elements.table.ExprSqlTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;

/**
 * Created by xuji on 2017/7/6.
 */
public class AtomicTableSource extends ExprSqlTable implements ExecutableTableSource {
    private final QueryReferFilter queryReferFilter;
    private final ConditionalSqlTable sqlTable;
    private final SqlTableRefers sqlTableRefers;

    public AtomicTableSource(LogicDbConfig logicDbConfig, QueryReferFilter queryReferFilter, SqlTableRefers sqlTableRefers) {
        super(logicDbConfig, (SQLExprTableSource) queryReferFilter.getReferTable().getSQLTableSource());
        this.sqlTableRefers = sqlTableRefers;
        this.sqlTable = queryReferFilter.getReferTable();
        this.queryReferFilter = queryReferFilter;
    }


    public SQLExprTableSource getSqlExprTableSource() {
        return super.getSQLTableSource();
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


}
