package org.the.force.jdbc.partition.engine.executor.query.tablesource;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.engine.executor.QueryExecution;
import org.the.force.jdbc.partition.engine.executor.query.QueryReferFilter;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.engine.executor.query.ExecutableTableSource;
import org.the.force.jdbc.partition.engine.executor.factory.BlockQueryExecutionFactory;
import org.the.force.jdbc.partition.engine.executor.factory.UnionQueryExecutionFactory;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.List;

/**
 * Created by xuji on 2017/6/4.
 */
public class SubQueriedTableSource extends ExecutableTableSource {
    private final SQLSubqueryTableSource subQueryTableSource;
    private final SqlTable sqlTable;
    private final QueryExecution queryExecution;

    //子查询预期的sqlTable
    public SubQueriedTableSource(LogicDbConfig logicDbConfig, SQLSubqueryTableSource subQueryTableSource, QueryReferFilter queryReferFilter) {
        super(logicDbConfig);
        this.subQueryTableSource = subQueryTableSource;
        this.sqlTable = queryReferFilter.getReferTable();
        super.setParent(subQueryTableSource.getParent());
        SQLSelectQuery sqlSelectQuery = subQueryTableSource.getSelect().getQuery();
        if (sqlSelectQuery == null) {
            throw new ParserException("sqlSelectQuery == null");
        }
        if (sqlSelectQuery instanceof SQLSelectQueryBlock) {
            queryExecution = new BlockQueryExecutionFactory(logicDbConfig, (SQLSelectQueryBlock) sqlSelectQuery, queryReferFilter).getQueryExecution();
        } else if (sqlSelectQuery instanceof SQLUnionQuery) {
            queryExecution = new UnionQueryExecutionFactory(logicDbConfig, (SQLUnionQuery) sqlSelectQuery, queryReferFilter).getQueryExecution();
        } else {
            throw new ParserException("un supported sql elements:" + PartitionSqlUtils.toSql(sqlSelectQuery, logicDbConfig.getSqlDialect()));
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PartitionSqlASTVisitor) {
            ((PartitionSqlASTVisitor) visitor).visit(this);
        } else {
            throw new SqlParseException("visitor not match");
        }
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

    public QueryExecution getQueryExecution() {
        return queryExecution;
    }

    public SqlTable getSqlTable() {
        return sqlTable;
    }

}
