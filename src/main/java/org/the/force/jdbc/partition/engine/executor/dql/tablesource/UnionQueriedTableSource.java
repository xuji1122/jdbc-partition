package org.the.force.jdbc.partition.engine.executor.dql.tablesource;

import org.the.force.jdbc.partition.engine.executor.QueryExecution;
import org.the.force.jdbc.partition.engine.executor.factory.UnionQueryExecutionFactory;
import org.the.force.jdbc.partition.engine.executor.dql.ExecutableTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.filter.QueryReferFilter;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/1.
 */
public class UnionQueriedTableSource extends SQLUnionQueryTableSource implements ExecutableTableSource {

    private final LogicDbConfig logicDbConfig;

    private final SQLUnionQueryTableSource sqlUnionQueryTableSource;

    private final SqlTable sqlTable;

    private final QueryExecution queryExecution;


    public UnionQueriedTableSource(LogicDbConfig logicDbConfig, QueryReferFilter queryReferFilter) {
        this.logicDbConfig = logicDbConfig;
        this.sqlTable = queryReferFilter.getReferTable();
        this.sqlUnionQueryTableSource = (SQLUnionQueryTableSource) sqlTable.getSQLTableSource();
        queryExecution = new UnionQueryExecutionFactory(logicDbConfig, sqlUnionQueryTableSource.getUnion(), queryReferFilter).getQueryExecution();
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PartitionSqlASTVisitor) {
            ((PartitionSqlASTVisitor) visitor).visit(this);
        } else {
            visitor.visit(sqlUnionQueryTableSource);
        }
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

    @Override
    public String getAlias() {
        return sqlUnionQueryTableSource.getAlias();
    }

    @Override
    public void setAlias(String alias) {
        //sqlUnionQueryTableSource.setAlias(alias);
    }

    @Override
    public int getHintsSize() {
        return sqlUnionQueryTableSource.getHintsSize();
    }

    @Override
    public void output(StringBuffer buf) {
        sqlUnionQueryTableSource.output(buf);
    }

    @Override
    public List<SQLHint> getHints() {
        return sqlUnionQueryTableSource.getHints();
    }

    @Override
    public SQLUnionQuery getUnion() {
        return sqlUnionQueryTableSource.getUnion();
    }

    @Override
    public void setUnion(SQLUnionQuery union) {
        //sqlUnionQueryTableSource.setUnion(union);
    }

    @Override
    public void setHints(List<SQLHint> hints) {
        //sqlUnionQueryTableSource.setHints(hints);
    }

    @Override
    public SQLTableSource clone() {
        return sqlUnionQueryTableSource.clone();
    }

    @Override
    public String toString() {
        return sqlUnionQueryTableSource.toString();
    }

    @Override
    public String computeAlias() {
        return sqlUnionQueryTableSource.computeAlias();
    }

    @Override
    public SQLExpr getFlashback() {
        return sqlUnionQueryTableSource.getFlashback();
    }

    @Override
    public void setFlashback(SQLExpr flashback) {
        //sqlUnionQueryTableSource.setFlashback(flashback);
    }

    @Override
    public SQLObject getParent() {
        return sqlUnionQueryTableSource.getParent();
    }

    @Override
    public void setParent(SQLObject parent) {
        //sqlUnionQueryTableSource.setParent(parent);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return sqlUnionQueryTableSource.getAttributes();
    }

    @Override
    public Object getAttribute(String name) {
        return sqlUnionQueryTableSource.getAttribute(name);
    }

    @Override
    public void putAttribute(String name, Object value) {
        sqlUnionQueryTableSource.putAttribute(name, value);
    }

    @Override
    public Map<String, Object> getAttributesDirect() {
        return sqlUnionQueryTableSource.getAttributesDirect();
    }

    @Override
    public void addBeforeComment(String comment) {
        sqlUnionQueryTableSource.addBeforeComment(comment);
    }

    @Override
    public void addBeforeComment(List<String> comments) {
        sqlUnionQueryTableSource.addBeforeComment(comments);
    }

    @Override
    public List<String> getBeforeCommentsDirect() {
        return sqlUnionQueryTableSource.getBeforeCommentsDirect();
    }

    @Override
    public void addAfterComment(String comment) {
        sqlUnionQueryTableSource.addAfterComment(comment);
    }

    @Override
    public void addAfterComment(List<String> comments) {
        sqlUnionQueryTableSource.addAfterComment(comments);
    }

    @Override
    public List<String> getAfterCommentsDirect() {
        return sqlUnionQueryTableSource.getAfterCommentsDirect();
    }

    @Override
    public boolean hasBeforeComment() {
        return sqlUnionQueryTableSource.hasBeforeComment();
    }

    @Override
    public boolean hasAfterComment() {
        return sqlUnionQueryTableSource.hasAfterComment();
    }
}
