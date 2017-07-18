package org.the.force.jdbc.partition.engine.executor.dql.tablesource;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.executor.dql.ExecutableTableSource;
import org.the.force.jdbc.partition.engine.executor.dql.filter.QueryReferFilter;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.elements.JoinConnector;
import org.the.force.jdbc.partition.engine.sql.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.sql.elements.SqlTableRefers;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlReferParser;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.engine.parser.table.TableConditionParser;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuji on 2017/6/4.
 * 平铺的JoinedTableSource 非嵌套
 */
public class ParallelJoinedTableSource extends SQLJoinTableSource implements ExecutableTableSource {

    private LogicDbConfig logicDbConfig;

    //输入

    private SQLJoinTableSource sqlJoinTableSource;

    //临时变量

    private final List<ConditionalSqlTable> sqlTables = new ArrayList<>();

    /**
     * 此条件会被设置到外部的selectQuery中去，JoinedTableSource不必实际的查询
     */
    private SQLExpr otherCondition;

    //输出

    private final Map<Pair<Integer, Integer>, JoinConnector> joinConnectorMap = new LinkedHashMap<>();

    private final Map<Integer, ExecutableTableSource> planedTableSourceMap = new LinkedHashMap<>();

    private Set<SQLExpr> conditionSet = new LinkedHashSet<>();


    public ParallelJoinedTableSource(LogicDbConfig logicDbConfig, SQLJoinTableSource sqlJoinTableSource, SQLExpr originalWhere) {
        this.logicDbConfig = logicDbConfig;
        this.sqlJoinTableSource = sqlJoinTableSource;
        super.alias = sqlJoinTableSource.getAlias();
        super.hints = sqlJoinTableSource.getHints();
        super.setFlashback(sqlJoinTableSource.getFlashback());
        super.setParent(sqlJoinTableSource.getParent());
        this.otherCondition = originalWhere;
        parseTableSource(sqlJoinTableSource);
        parseTableCondition();
    }

    private void parseTableSource(SQLJoinTableSource sqlJoinTableSource) {
        SQLTableSource left = sqlJoinTableSource.getLeft();
        SQLTableSource right = sqlJoinTableSource.getRight();
        if (left instanceof SQLJoinTableSource) {
            parseTableSource((SQLJoinTableSource) left);
        } else {
            left.setParent(this);
            ConditionalSqlTable sqlTable = new SqlTableParser(logicDbConfig).getSqlTable(left);
            sqlTables.add(sqlTable);
        }
        right.setParent(this);
        ConditionalSqlTable sqlTable = new SqlTableParser(logicDbConfig).getSqlTable(right);
        sqlTables.add(sqlTable);
        JoinConnector joinConnector = new JoinConnector(sqlJoinTableSource.getJoinType(), sqlJoinTableSource.getCondition());
        Pair<Integer, Integer> pair = new Pair<>(sqlTables.size() - 2, sqlTables.size() - 1);
        if (joinConnector.getJoinCondition() != null) {
            conditionSet.add(joinConnector.getJoinCondition());
        }
        joinConnectorMap.put(pair, joinConnector);
    }

    private void parseTableCondition() {
        int size = sqlTables.size();
        for (int i = 0; i < size; i++) {
            ConditionalSqlTable sqlTable = sqlTables.get(i);
            SQLTableSource sqlTableSource = sqlTable.getSQLTableSource();
            TableConditionParser parser = new TableConditionParser(logicDbConfig, this.otherCondition, i, sqlTables);
            this.otherCondition = parser.getOtherCondition();
            SQLExpr tableOwnCondition = sqlTable.getTableOwnCondition();

            Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> conditionTableMap = sqlTable.getJoinConditionMap();
            conditionTableMap.forEach((pair, list) -> {
                Pair<Integer, Integer> key = pair;
                List<SQLBinaryOpExpr> joinConditions = conditionTableMap.get(key);
                JoinConnector connector = joinConnectorMap.get(key);
                if (joinConditions != null) {
                    for (SQLBinaryOpExpr sqlBinaryOpExpr : joinConditions) {
                        if (!conditionSet.contains(sqlBinaryOpExpr)) {
                            connector = joinConnectorMap.get(key);
                            SQLExpr mergeExpr = mergeLogicalCondition(connector.getJoinCondition(), sqlBinaryOpExpr);
                            joinConnectorMap.put(key, new JoinConnector(connector.getJoinType(), mergeExpr));
                            conditionSet.add(sqlBinaryOpExpr);
                        }
                    }
                }
            });
            JoinConnector joinConnector;
            if (i < size - 1) {
                joinConnector = joinConnectorMap.get(new Pair<>(i, i + 1));
            } else {
                joinConnector = joinConnectorMap.get(new Pair<>(size - 2, size - 1));
            }
            if (joinConnector == null || joinConnector.getJoinCondition() == null) {
                throw new SqlParseException("表格拼接必须制定join的条件");
            }
            List<SqlRefer> sqlOrderByItemForJoin = new SqlReferParser(joinConnector.getJoinCondition(), sqlTable).getSqlReferList();
            if (sqlOrderByItemForJoin.isEmpty()) {
                throw new SqlParseException("join的条件sqlProperties.isEmpty()");
            }
            QueryReferFilter queryReferFilter = new QueryReferFilter(logicDbConfig, sqlTable);
            queryReferFilter.getOrderBySqlRefersForJoin().addAll(sqlOrderByItemForJoin);
            ExecutableTableSource executableTableSource;
            if (sqlTableSource instanceof SQLExprTableSource) {
                SqlTableRefers sqlTableRefers = new SqlTableReferParser(logicDbConfig, sqlJoinTableSource.getParent(), sqlTable).getSqlTableRefers();
                executableTableSource = new AtomicTableSource(logicDbConfig, queryReferFilter, sqlTableRefers);
            } else if (sqlTableSource instanceof SQLJoinTableSource) {
                throw new ParserException("SQLJoinTableSource 不应出现");
            } else if (sqlTableSource instanceof SQLSubqueryTableSource) {
                executableTableSource = new SubQueriedTableSource(logicDbConfig, queryReferFilter);
            } else if (sqlTableSource instanceof SQLUnionQueryTableSource) {
                executableTableSource = new UnionQueriedTableSource(logicDbConfig, queryReferFilter);
            } else {
                //TODO
                throw new SqlParseException("无法识别的tableSource类型" + sqlTableSource.getClass().getName());
            }
            planedTableSourceMap.put(i, executableTableSource);
        }

    }

    private SQLExpr mergeLogicalCondition(SQLExpr left, SQLExpr right) {
        SQLExpr mergeExpr = null;
        if (left != null && right != null) {
            mergeExpr = new SQLBinaryOpExpr(left, SQLBinaryOperator.BooleanAnd, right, logicDbConfig.getSqlDialect().getDruidSqlDialect());

        } else if (left != null) {
            mergeExpr = left;

        } else if (right != null) {
            mergeExpr = right;
        }
        return mergeExpr;
    }


    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PartitionSqlASTVisitor) {
            PartitionSqlASTVisitor partitionSqlASTVisitor = (PartitionSqlASTVisitor) visitor;
            if (partitionSqlASTVisitor.visit(this)) {
                planedTableSourceMap.values().forEach(executableTableSource -> executableTableSource.accept(visitor));
                conditionSet.forEach(sqlExpr -> sqlExpr.accept(visitor));
            }
        } else {
            sqlJoinTableSource.accept(visitor);
        }
    }

    public Set<SQLExpr> getConditionSet() {
        return conditionSet;
    }

    public Map<Integer, ExecutableTableSource> getPlanedTableSourceMap() {
        return planedTableSourceMap;
    }

    public List<ConditionalSqlTable> getSqlTables() {
        return sqlTables;
    }

    public SQLExpr getOtherCondition() {
        return otherCondition;
    }


    public JoinType getJoinType() {
        throw new UnsupportedOperationException(this.getClass().getName());
    }

    public void setJoinType(JoinType joinType) {
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
