package org.the.force.jdbc.partition.engine.executor.dql.factory;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.JoinedTableSourceExecutor;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlReferParser;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlTableReferParser;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.engine.parser.table.TableConditionParser;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.JoinConnector;
import org.the.force.jdbc.partition.engine.sql.SqlRefer;
import org.the.force.jdbc.partition.engine.sql.SqlTableRefers;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLOrderBy;
import org.the.force.thirdparty.druid.sql.ast.SQLOrderingSpecification;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLPropertyExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectOrderByItem;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by xuji on 2017/6/4.
 * 平铺的JoinedTableSource 非嵌套
 */
public class JoinedTableSourceFactory {

    private LogicDbConfig logicDbConfig;

    //临时变量

    private final List<ConditionalSqlTable> sqlTables = new ArrayList<>();

    /**
     * 此条件会被设置到外部的selectQuery中去，JoinedTableSource不必实际的查询
     */
    private SQLExpr otherCondition;

    private final Map<Pair<Integer, Integer>, JoinConnector> joinConnectorMap = new LinkedHashMap<>();

    private final List<QueryExecutor> queryExecutors = new ArrayList<>();

    private Set<SQLExpr> conditionSet = new LinkedHashSet<>();

    //输出
    private JoinedTableSourceExecutor joinedTableSourceExecutor;

    public JoinedTableSourceFactory(LogicDbConfig logicDbConfig, SQLJoinTableSource sqlJoinTableSource, SQLExpr originalWhere) {
        this.logicDbConfig = logicDbConfig;
        this.otherCondition = originalWhere;
        joinedTableSourceExecutor = new JoinedTableSourceExecutor(logicDbConfig, sqlJoinTableSource);
        parseTableSource(joinedTableSourceExecutor.getSqlJoinTableSource());
        parseTableCondition();
    }

    private void parseTableSource(SQLJoinTableSource sqlJoinTableSource) {
        SQLTableSource left = sqlJoinTableSource.getLeft();
        SQLTableSource right = sqlJoinTableSource.getRight();
        if (left instanceof SQLJoinTableSource) {
            parseTableSource((SQLJoinTableSource) left);
        } else {
            left.setParent(joinedTableSourceExecutor);
            ConditionalSqlTable sqlTable = new SqlTableParser(logicDbConfig).getSqlTable(left);
            sqlTables.add(sqlTable);
        }
        right.setParent(joinedTableSourceExecutor);
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
            TableConditionParser parser = new TableConditionParser(logicDbConfig, this.otherCondition, i, sqlTables);
            this.otherCondition = parser.getOtherCondition();
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
                throw new SqlParseException("表格拼接必须指定join的条件");
            }
            List<SqlRefer> sqlOrderByItemForJoin = new SqlReferParser(joinConnector.getJoinCondition(), sqlTable).getSqlReferList();
            if (sqlOrderByItemForJoin.isEmpty()) {
                throw new SqlParseException("join的条件sqlProperties.isEmpty()");
            }
            SqlTableRefers sqlTableRefers = new SqlTableReferParser(logicDbConfig, joinedTableSourceExecutor.getSqlJoinTableSource().getParent(), sqlTable).getSqlTableRefers();
            SQLSelectQueryBlock sqlSelectQueryBlock = buildSQLSelectQueryBlock(sqlTable, sqlTableRefers, sqlOrderByItemForJoin);
            QueryExecutor queryExecutor = new BlockQueryExecutorFactory(logicDbConfig, sqlSelectQueryBlock).build();
            queryExecutor.setParent(joinedTableSourceExecutor.getSqlJoinTableSource().getParent());
            queryExecutors.add(queryExecutor);
        }

    }

    private SQLSelectQueryBlock buildSQLSelectQueryBlock(ConditionalSqlTable sqlTable, SqlTableRefers sqlTableRefers, List<SqlRefer> sqlOrderByItemForJoin) {
        MySqlSelectQueryBlock mySqlSelectQueryBlock = new MySqlSelectQueryBlock();
        mySqlSelectQueryBlock.setParent(joinedTableSourceExecutor.getSqlJoinTableSource());
        mySqlSelectQueryBlock.setFrom(sqlTable.getSQLTableSource());
        if (sqlTable.getAlias() != null) {
            sqlTable.getSQLTableSource().setAlias(sqlTable.getAlias());
        }
        List<SQLSelectItem> selectList = new ArrayList<>();
        if (sqlTableRefers.isReferAll()) {
            List<String> columns = sqlTable.getReferLabels();
            if (columns == null || columns.isEmpty()) {
                selectList.add(new SQLSelectItem(new SQLAllColumnExpr()));
            } else {
                for (String column : columns) {
                    if (sqlTable.getAlias() != null) {
                        SQLPropertyExpr sqlPropertyExpr = new SQLPropertyExpr(sqlTable.getAlias(), column);
                        selectList.add(new SQLSelectItem(sqlPropertyExpr));
                    } else {
                        SQLIdentifierExpr sqlIdentifierExpr = new SQLIdentifierExpr(column);
                        selectList.add(new SQLSelectItem(sqlIdentifierExpr));
                    }
                }
            }
        } else {
            selectList.addAll(sqlTableRefers.getReferLabels().stream().map(sqlRefer -> new SQLSelectItem(new SQLIdentifierExpr(sqlRefer))).collect(Collectors.toList()));
        }
        mySqlSelectQueryBlock.getSelectList().addAll(selectList);
        if (sqlTable.getTableOwnCondition() != null) {
            mySqlSelectQueryBlock.setWhere(sqlTable.getTableOwnCondition());
        }
        SQLOrderBy orderBy = new SQLOrderBy();
        for (SqlRefer sqlRefer : sqlOrderByItemForJoin) {
            SQLSelectOrderByItem orderByItem = new SQLSelectOrderByItem();
            orderByItem.setType(SQLOrderingSpecification.DESC);
            orderByItem.setNullsOrderType(SQLSelectOrderByItem.NullsOrderType.NullsLast);
            if (sqlTable.getAlias() != null) {
                orderByItem.setExpr(new SQLPropertyExpr(sqlTable.getAlias(), sqlRefer.getName()));
            } else {
                orderByItem.setExpr(new SQLIdentifierExpr(sqlRefer.getName()));
            }
            orderBy.getItems().add(orderByItem);
        }
        mySqlSelectQueryBlock.setOrderBy(orderBy);
        return mySqlSelectQueryBlock;
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


    public Set<SQLExpr> getConditionSet() {
        return conditionSet;
    }

    public SQLExpr getOtherCondition() {
        return otherCondition;
    }

    public JoinedTableSourceExecutor getJoinedTableSourceExecutor() {
        return joinedTableSourceExecutor;
    }
}
