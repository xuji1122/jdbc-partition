package org.the.force.jdbc.partition.engine.executor.dql.tablesource;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.BlockQueryExecutor;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.JoinConnector;
import org.the.force.jdbc.partition.engine.value.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/18.
 * TODO
 * 表关联查询
 *  1，逻辑上拆成了多个表  binding关系
 *  2，join的条件其中一个表已经指定了value  优化join条件的问题
 *  3，小表广播的方式
 */
public class JoinedTableSourceExecutor extends SQLJoinTableSource implements BlockQueryExecutor {

    private final LogicDbConfig logicDbConfig;

    private final SQLJoinTableSource sqlJoinTableSource;

    private final List<ConditionalSqlTable> sqlTables = new ArrayList<>();

    private final List<QueryExecutor> queryExecutors = new ArrayList<>();

    private final Map<Pair<Integer, Integer>, JoinConnector> joinConnectorMap = new LinkedHashMap<>();


    public JoinedTableSourceExecutor(LogicDbConfig logicDbConfig, SQLJoinTableSource sqlJoinTableSource) {
        this.logicDbConfig = logicDbConfig;
        this.sqlJoinTableSource = sqlJoinTableSource;
        this.setParent(sqlJoinTableSource.getParent());
    }

    public ResultSet execute(QueryCommand queryCommand, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
        return null;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PartitionSqlASTVisitor) {
            PartitionSqlASTVisitor partitionSqlASTVisitor = (PartitionSqlASTVisitor) visitor;
            partitionSqlASTVisitor.visit(this);
        } else {
            sqlJoinTableSource.accept(visitor);
        }
    }

    public void add(ConditionalSqlTable conditionalSqlTable) {
        conditionalSqlTable.getSQLTableSource().setParent(this);
        sqlTables.add(conditionalSqlTable);
    }

    public List<ConditionalSqlTable> getSqlTables() {
        return sqlTables;
    }


    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public SQLJoinTableSource getSqlJoinTableSource() {
        return sqlJoinTableSource;
    }

    public Map<Pair<Integer, Integer>, JoinConnector> getJoinConnectorMap() {
        return joinConnectorMap;
    }

    public List<QueryExecutor> getQueryExecutors() {
        return queryExecutors;
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
        return sqlJoinTableSource.getUsing();
    }

    public boolean isNatural() {
        return sqlJoinTableSource.isNatural();
    }

    public void setNatural(boolean natural) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public void output(StringBuffer buf) {
        sqlJoinTableSource.output(buf);
    }

    public boolean equals(Object o) {
        return sqlJoinTableSource.equals(o);
    }

    public boolean replace(SQLExpr expr, SQLExpr target) {
        throw new UnsupportedOperationException(this.getClass().getName());
    }



}
