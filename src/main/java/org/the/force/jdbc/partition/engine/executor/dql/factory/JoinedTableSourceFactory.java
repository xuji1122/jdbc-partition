package org.the.force.jdbc.partition.engine.executor.dql.factory;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.JoinedTableSource;
import org.the.force.jdbc.partition.engine.parser.copy.SqlObjCopier;
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
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.SQLOrderBy;
import org.the.force.thirdparty.druid.sql.ast.SQLOrderingSpecification;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLAllColumnExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLIdentifierExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
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

    /**
     * 临时变量
     */
    private final List<ConditionalSqlTable> sqlTables = new ArrayList<>();

    private Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> joinConditionMap = new LinkedHashMap<>();

    //此条件会被设置到外部的selectQuery中去，JoinedTableSource不必实际的查询
    private SQLExpr otherCondition;

    private final Map<Pair<Integer, Integer>, JoinConnector> joinConnectorMap = new LinkedHashMap<>();

    private final List<QueryExecutor> queryExecutors = new ArrayList<>();

    /**
     * 输出
     */
    private JoinedTableSource joinedTableSource;

    public JoinedTableSourceFactory(LogicDbConfig logicDbConfig, SQLJoinTableSource sqlJoinTableSource, SQLExpr originalWhere) {
        this.logicDbConfig = logicDbConfig;
        this.otherCondition = originalWhere;
        joinedTableSource = new JoinedTableSource(logicDbConfig, sqlJoinTableSource);
        parseTableSource(joinedTableSource.getSqlJoinTableSource());
        parseCondition();
        buildJoin();
        joinedTableSource.getSqlTables().addAll(sqlTables);
        joinedTableSource.getQueryExecutors().addAll(this.queryExecutors);
        joinConnectorMap.values().forEach(joinConnector -> joinedTableSource.getJoinConnectorList().add(joinConnector));
    }

    private void parseTableSource(SQLJoinTableSource sqlJoinTableSource) {
        SQLTableSource left = sqlJoinTableSource.getLeft();
        SQLTableSource right = sqlJoinTableSource.getRight();
        if (left instanceof SQLJoinTableSource) {
            parseTableSource((SQLJoinTableSource) left);
        } else {
            left.setParent(joinedTableSource);
            ConditionalSqlTable sqlTable = new SqlTableParser(logicDbConfig).getSqlTable(left);
            sqlTables.add(sqlTable);
        }
        right.setParent(joinedTableSource);
        ConditionalSqlTable sqlTable = new SqlTableParser(logicDbConfig).getSqlTable(right);
        sqlTables.add(sqlTable);
        JoinConnector joinConnector = new JoinConnector(sqlJoinTableSource.getJoinType(), sqlJoinTableSource.getCondition());
        Pair<Integer, Integer> pair = new Pair<>(sqlTables.size() - 2, sqlTables.size() - 1);
        joinConnectorMap.put(pair, joinConnector);
    }

    private void parseCondition() {
        int size = sqlTables.size();
        for (int i = 0; i < size; i++) {
            Pair<Integer, Integer> expectPair;
            if (i < size - 1) {
                expectPair = new Pair<>(i, i + 1);
            } else {
                expectPair = new Pair<>(size - 2, size - 1);
            }
            JoinConnector joinConnector = joinConnectorMap.get(expectPair);
            if (joinConnector.getJoinCondition() != null) {
                TableConditionParser parser = new TableConditionParser(logicDbConfig, joinConnector.getJoinCondition(), i, sqlTables);
                SQLExpr newCondition = parser.getOtherCondition();
                JoinConnector newJoinConnector = new JoinConnector(joinConnector.getJoinType(), newCondition);
                joinConnectorMap.put(expectPair, newJoinConnector);
                addJoinSQLBinaryOpExpr(parser.getJoinConditionMap());
            }
            TableConditionParser parser = new TableConditionParser(logicDbConfig, this.otherCondition, i, sqlTables);
            this.otherCondition = parser.getOtherCondition();
            addJoinSQLBinaryOpExpr(parser.getJoinConditionMap());

        }
    }

    private void addJoinSQLBinaryOpExpr(Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> conditionTableMap) {
        conditionTableMap.forEach((pair, list) -> {
            List<SQLBinaryOpExpr> binaryOpExprList = joinConditionMap.get(pair);
            if (binaryOpExprList == null) {
                binaryOpExprList = new ArrayList<>();
                joinConditionMap.put(pair, binaryOpExprList);
            }
            for (SQLBinaryOpExpr sqlBinaryOpExpr : list) {
                if (!binaryOpExprList.contains(sqlBinaryOpExpr)) {
                    binaryOpExprList.add(sqlBinaryOpExpr);
                }
            }
            JoinConnector joinConnector = joinConnectorMap.get(pair);
            if (joinConnector != null && joinConnector.getJoinType() == SQLJoinTableSource.JoinType.COMMA) {
                joinConnectorMap.put(pair, new JoinConnector(SQLJoinTableSource.JoinType.INNER_JOIN, joinConnector.getJoinCondition()));
            }

        });
    }

    private void buildJoin() {
        Set<SQLExpr> conditionSet = new LinkedHashSet<>();
        int size = sqlTables.size();
        for (int i = 0; i < size; i++) {
            ConditionalSqlTable sqlTable = sqlTables.get(i);
            Pair<Integer, Integer> expectPair;
            if (i < size - 1) {
                expectPair = new Pair<>(i, i + 1);
                conditionSet.clear();
            } else {
                expectPair = new Pair<>(size - 2, size - 1);
            }
            //可用的join条件分析
            joinConditionMap.forEach((pair, list) -> {
                Pair<Integer, Integer> key = pair;
                if (expectPair.getRight() >= key.getRight()) {
                    list.forEach(sqlBinaryOpExpr -> {
                        if (!conditionSet.contains(sqlBinaryOpExpr)) {
                            JoinConnector joinConnector = joinConnectorMap.get(expectPair);
                            SQLExpr mergeExpr = mergeLogicalCondition(joinConnector.getJoinCondition(), sqlBinaryOpExpr);
                            joinConnectorMap.put(key, new JoinConnector(joinConnector.getJoinType(), mergeExpr));
                            conditionSet.add(sqlBinaryOpExpr);
                        }
                    });
                }
            });
            JoinConnector joinConnector = joinConnectorMap.get(expectPair);
            if (joinConnector == null || joinConnector.getJoinCondition() == null) {
                throw new SqlParseException("表格join必须指定join的条件");
            }
            //join的条件推到的列等价条件分析
            Map<SqlRefer, List<Pair<ConditionalSqlTable, SqlRefer>>> equalReferMap = sqlTable.getEqualReferMap();
            equalReferMap.forEach(((sqlRefer, pairs) -> {
                pairs.forEach(pair -> {
                    ConditionalSqlTable joinedSqlTable = pair.getLeft();
                    SqlRefer joinedTableColumn = pair.getRight();
                    Map<SqlRefer, List<SqlExprEvaluator>> sqlExprEvaluatorMap = joinedSqlTable.getColumnConditionsMap();
                    List<SqlExprEvaluator> list = sqlExprEvaluatorMap.get(joinedTableColumn);
                    if (list != null) {
                        list.forEach(sqlExprEvaluator -> {

                            addEqualCondition(sqlTable, sqlExprEvaluator.getOriginalSqlExpr(), (SQLName) joinedTableColumn.getOriginalSqlExpr(),
                                (SQLName) sqlRefer.getOriginalSqlExpr());
                        });
                    }
                    Map<List<SQLExpr>, SQLInListEvaluator> columnInListConditionMap = joinedSqlTable.getColumnInListConditionMap();
                    if (columnInListConditionMap != null && !columnInListConditionMap.isEmpty()) {
                        List<SQLInListEvaluator> sqlInListEvaluators = columnInListConditionMap.entrySet().stream()
                            .filter(entry -> entry.getKey() != null && entry.getKey().size() == 1 && entry.getKey().contains(joinedTableColumn)).map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                        sqlInListEvaluators.forEach(sqlInListEvaluator -> {
                            addEqualCondition(sqlTable, sqlInListEvaluator.getOriginalSqlExpr(), (SQLName) joinedTableColumn.getOriginalSqlExpr(),
                                (SQLName) sqlRefer.getOriginalSqlExpr());
                        });
                    }
                });
            }));
            //join需要的单表的排序条件分析
            List<SqlRefer> sqlOrderByItemForJoin = new SqlReferParser(joinConnector.getJoinCondition(), sqlTable).getSqlReferList();
            if (sqlOrderByItemForJoin.isEmpty()) {
                throw new SqlParseException("join的条件sqlProperties.isEmpty()");
            }
            SqlTableRefers sqlTableRefers = new SqlTableReferParser(logicDbConfig, joinedTableSource.getSqlJoinTableSource().getParent(), sqlTable).getSqlTableRefers();
            SQLSelectQueryBlock sqlSelectQueryBlock = buildSQLSelectQueryBlock(sqlTable, sqlTableRefers, sqlOrderByItemForJoin);
            QueryExecutor queryExecutor = new BlockQueryExecutorFactory(logicDbConfig, sqlSelectQueryBlock).build();
            queryExecutor.setParent(joinedTableSource.getSqlJoinTableSource().getParent());
            queryExecutors.add(queryExecutor);
        }

    }

    private SQLSelectQueryBlock buildSQLSelectQueryBlock(ConditionalSqlTable sqlTable, SqlTableRefers sqlTableRefers, List<SqlRefer> sqlOrderByItemForJoin) {
        MySqlSelectQueryBlock mySqlSelectQueryBlock = new MySqlSelectQueryBlock();
        mySqlSelectQueryBlock.setParent(joinedTableSource.getSqlJoinTableSource());
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

    private void addEqualCondition(ConditionalSqlTable sqlTable, SQLExpr condition, SQLName from, SQLName to) {
        SqlObjCopier sqlObjCopier = new SqlObjCopier();
        sqlObjCopier.addReplaceObj(from, to);
        sqlObjCopier.setUseEqualsWhenReplace(true);
        try {
            SQLExpr newCondition = sqlObjCopier.copy(condition);
            SqlRefer sqlRefer = new SqlRefer(to);
            if (condition instanceof SQLInListExpr) {
                SQLInListEvaluator sqlExprEvaluator = (SQLInListEvaluator) logicDbConfig.getSqlExprEvaluatorFactory().matchSqlExprEvaluator(newCondition);
                List<SQLExpr> listKey = new ArrayList<>();
                listKey.add(sqlRefer);
                sqlTable.getColumnInListConditionMap().put(listKey, sqlExprEvaluator);
            } else {
                TableConditionParser.addColumnCondition(sqlTable, sqlRefer, newCondition, logicDbConfig);
            }
            SQLExpr newTableCondition = mergeLogicalCondition(sqlTable.getTableOwnCondition(), newCondition);
            sqlTable.setTableOwnCondition(newTableCondition);
        } catch (Exception e) {
            throw new SqlParseException("");
        }
    }

    public SQLExpr getOtherCondition() {
        return otherCondition;
    }

    public JoinedTableSource getJoinedTableSource() {
        return joinedTableSource;
    }
}
