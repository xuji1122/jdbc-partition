package org.the.force.jdbc.partition.engine.executor.dql.tablesource;

import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.LogicTableSource;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.JoinConnector;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelect;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/18.
 * TODO
 * 表关联查询
 * 1，逻辑上拆成了多个表  binding关系
 * 2，join的条件其中一个表已经指定了value  优化join条件的问题
 * 3，小表广播的方式
 */
public class JoinedTableSource extends SQLJoinTableSource implements LogicTableSource {

    private final LogicDbConfig logicDbConfig;

    private final SQLJoinTableSource originalJoinTableSource;

    private final List<ConditionalSqlTable> sqlTables = new ArrayList<>();

    //queryExecutors的size和sqlTables的size相等且对应
    private final List<QueryExecutor> queryExecutors = new ArrayList<>();

    //joinConnectorList size是queryExecutors的size减去1
    private final List<JoinConnector> joinConnectorList = new ArrayList<>();


    public JoinedTableSource(LogicDbConfig logicDbConfig, SQLJoinTableSource originalJoinTableSource) {
        this.logicDbConfig = logicDbConfig;
        this.originalJoinTableSource = originalJoinTableSource;
        this.setParent(originalJoinTableSource.getParent());
    }


    //打印调试时输出原始的join关系
    protected void accept0(SQLASTVisitor visitor) {
        SQLTableSource left = buildSQLSubqueryTableSource(sqlTables.get(0), queryExecutors.get(0));
        for (int i = 1; i < queryExecutors.size(); i++) {
            SQLSubqueryTableSource right = buildSQLSubqueryTableSource(sqlTables.get(i), queryExecutors.get(i));
            left = buildSQLJoinTableSource(left, right, joinConnectorList.get(i - 1));
        }
        left.accept(visitor);
    }

    private SQLSubqueryTableSource buildSQLSubqueryTableSource(ConditionalSqlTable conditionalSqlTable, QueryExecutor queryExecutor) {
        SQLSelect sqlSelect = new SQLSelect(queryExecutor.getStatement());
        SQLSubqueryTableSource subqueryTableSource = new SQLSubqueryTableSource(sqlSelect);
        subqueryTableSource.setAlias(conditionalSqlTable.getRelativeKey());
        return subqueryTableSource;
    }

    private SQLJoinTableSource buildSQLJoinTableSource(SQLTableSource left, SQLSubqueryTableSource sqlSubqueryTableSource, JoinConnector joinConnector) {
        return new SQLJoinTableSource(left, joinConnector.getJoinType(), sqlSubqueryTableSource, joinConnector.getJoinCondition());
    }

    public List<ConditionalSqlTable> getSqlTables() {
        return sqlTables;
    }

    public List<JoinConnector> getJoinConnectorList() {
        return joinConnectorList;
    }

    public List<QueryExecutor> getQueryExecutors() {
        return queryExecutors;
    }

    public void addFirst(ConditionalSqlTable conditionalSqlTable, QueryExecutor queryExecutor) {
        if (joinConnectorList.isEmpty() && sqlTables.isEmpty() && queryExecutors.isEmpty()) {
            sqlTables.add(conditionalSqlTable);
            queryExecutors.add(queryExecutor);
        }
    }

    public void addJoinedTable(ConditionalSqlTable conditionalSqlTable, QueryExecutor queryExecutor, JoinConnector joinConnector) {
        int size = sqlTables.size();
        if (size < 1) {
            throw new RuntimeException("addJoinedTable size<1");
        }
        if (queryExecutors.size() != size) {
            throw new RuntimeException("queryExecutors.size() != size");
        }
        if (joinConnectorList.size() != size - 1) {
            throw new RuntimeException("joinConnectorList.size() != size - 1");
        }
        if (sqlTables.contains(conditionalSqlTable)) {
            throw new RuntimeException("sqlTables.contains(conditionalSqlTable)");
        }
        sqlTables.add(conditionalSqlTable);
        queryExecutors.add(queryExecutor);
        joinConnectorList.add(joinConnector);
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public SQLJoinTableSource getOriginalJoinTableSource() {
        return originalJoinTableSource;
    }

    public SQLJoinTableSource.JoinType getJoinType() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public void setJoinType(SQLJoinTableSource.JoinType joinType) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public SQLTableSource getLeft() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public void setLeft(SQLTableSource left) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public SQLTableSource getRight() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public void setRight(SQLTableSource right) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public SQLExpr getCondition() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public void setCondition(SQLExpr condition) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public List<SQLExpr> getUsing() {
        return originalJoinTableSource.getUsing();
    }

    public boolean isNatural() {
        return originalJoinTableSource.isNatural();
    }

    public void setNatural(boolean natural) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public void output(StringBuffer buf) {
        originalJoinTableSource.output(buf);
    }

    public boolean equals(Object o) {
        return originalJoinTableSource.equals(o);
    }

    public boolean replace(SQLExpr expr, SQLExpr target) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }


}
