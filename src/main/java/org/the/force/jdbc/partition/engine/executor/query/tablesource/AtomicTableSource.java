package org.the.force.jdbc.partition.engine.executor.query.tablesource;

import org.the.force.jdbc.partition.engine.executor.query.QueryReferFilter;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumn;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTableRefers;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.executor.query.ExecutableTableSource;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.Map;

/**
 * Created by xuji on 2017/7/6.
 */
public class AtomicTableSource extends ExecutableTableSource {

    private final SQLExprTableSource sqlExprTableSource;
    private final Map<SqlColumn, SQLExpr> columnValueMap;
    private final Map<SqlColumn, SQLInListExpr> columnInValuesMap;
    private final QueryReferFilter queryReferFilter;
    private final SqlTable sqlTable;
    private final SqlTableRefers sqlTableRefers;

    public AtomicTableSource(LogicDbConfig logicDbConfig, SQLExprTableSource sqlExprTableSource, Map<SqlColumn, SQLExpr> columnValueMap,
        Map<SqlColumn, SQLInListExpr> columnInValuesMap, QueryReferFilter queryReferFilter) {
        super(logicDbConfig);
        this.sqlExprTableSource = sqlExprTableSource;
        this.sqlTable = queryReferFilter.getReferTable();
        this.columnValueMap = columnValueMap;
        this.columnInValuesMap = columnInValuesMap;
        SqlTableReferParser parser = new SqlTableReferParser(logicDbConfig, sqlExprTableSource, sqlTable);
        sqlTableRefers = parser.getSqlTableRefers();
        this.queryReferFilter = queryReferFilter;

    }

    public SQLExprTableSource getSqlExprTableSource() {
        return sqlExprTableSource;
    }

    public SqlTable getSqlTable() {
        return sqlTable;
    }


    public SqlTableRefers getSqlTableRefers() {
        return sqlTableRefers;
    }

    public Map<SqlColumn, SQLExpr> getColumnValueMap() {
        return columnValueMap;
    }

    public Map<SqlColumn, SQLInListExpr> getColumnInValuesMap() {
        return columnInValuesMap;
    }


    public QueryReferFilter getQueryReferFilter() {
        return queryReferFilter;
    }

    protected void accept0(SQLASTVisitor visitor) {

    }
}
