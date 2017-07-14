package org.the.force.jdbc.partition.engine.executor.query.tablesource;

import org.the.force.jdbc.partition.engine.executor.QueryExecution;
import org.the.force.jdbc.partition.engine.executor.factory.UnionQueryExecutionFactory;
import org.the.force.jdbc.partition.engine.executor.query.ExecutableTableSource;
import org.the.force.jdbc.partition.engine.executor.query.QueryReferFilter;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.List;

/**
 * Created by xuji on 2017/7/1.
 */
public class UnionQueriedTableSource extends ExecutableTableSource {

    private final SQLUnionQueryTableSource sqlUnionQueryTableSource;

    private final SqlTable sqlTable;

    private final QueryExecution queryExecution;


    public UnionQueriedTableSource(LogicDbConfig logicDbConfig, SQLUnionQueryTableSource sqlUnionQueryTableSource, QueryReferFilter queryReferFilter) {
        super(logicDbConfig);
        this.sqlUnionQueryTableSource = sqlUnionQueryTableSource;
        super.setParent(sqlUnionQueryTableSource.getParent());
        this.sqlTable = queryReferFilter.getReferTable();
        queryExecution = new UnionQueryExecutionFactory(logicDbConfig, sqlUnionQueryTableSource.getUnion(), queryReferFilter).getQueryExecution();
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PartitionSqlASTVisitor) {
            ((PartitionSqlASTVisitor) visitor).visit(this);
        } else {
            throw new SqlParseException("visitor not match");
        }
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

    public QueryExecution getQueryExecution() {
        return queryExecution;
    }
}
