package org.the.force.jdbc.partition.engine.executor.query.tablesource;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.executor.query.QueryReferFilter;
import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlReferParser;
import org.the.force.jdbc.partition.engine.parser.visitor.PartitionSqlASTVisitor;
import org.the.force.jdbc.partition.engine.executor.query.ExecutableTableSource;
import org.the.force.jdbc.partition.engine.parser.table.TableConditionParser;
import org.the.force.jdbc.partition.engine.parser.elements.JoinConnector;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumn;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.table.SqlTableParser;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOperator;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLJoinTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSubqueryTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLUnionQueryTableSource;
import org.the.force.thirdparty.druid.sql.parser.ParserException;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuji on 2017/6/4.
 */
public class JoinedTableSource extends ExecutableTableSource {

    private final List<SQLTableSource> originalTableSources = new ArrayList<>();

    private final List<SqlTable> sqlTables = new ArrayList<>();

    private final Map<Pair<Integer, Integer>, JoinConnector> joinConnectorMap = new LinkedHashMap<>();

    private final Map<Integer, ExecutableTableSource> planedTableSourceMap = new HashMap<>();

    private SQLExpr otherCondition;

    private Set<SQLExpr> conditionSet = new HashSet<>();


    public JoinedTableSource(LogicDbConfig logicDbConfig, SQLJoinTableSource sqlJoinTableSource, SQLExpr originalWhere) {
        super(logicDbConfig);
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
            left.setParent(this.getParent());
            SqlTable sqlTable = new SqlTableParser(logicDbConfig).getSqlTable(left);
            sqlTables.add(sqlTable);
            originalTableSources.add(left);
        }
        right.setParent(this.getParent());
        SqlTable sqlTable = new SqlTableParser(logicDbConfig).getSqlTable(right);
        sqlTables.add(sqlTable);
        originalTableSources.add(right);
        JoinConnector joinConnector = new JoinConnector(sqlJoinTableSource.getJoinType(), sqlJoinTableSource.getCondition());
        Pair<Integer, Integer> pair = new Pair<>(originalTableSources.size() - 2, originalTableSources.size() - 1);
        if (joinConnector.getJoinCondition() != null) {
            conditionSet.add(joinConnector.getJoinCondition());
        }
        joinConnectorMap.put(pair, joinConnector);
    }

    private void parseTableCondition() {
        int size = sqlTables.size();
        for (int i = 0; i < size; i++) {
            SQLTableSource sqlTableSource = originalTableSources.get(i);
            SqlTable sqlTable = sqlTables.get(i);
            TableConditionParser parser = new TableConditionParser(logicDbConfig, this.otherCondition, i, sqlTables);
            this.otherCondition = parser.getOtherCondition();
            SQLExpr tableOwnCondition = parser.getCurrentTableOwnCondition();
            Map<SqlColumn, SQLExpr> currentTableColumnValueMap = parser.getCurrentTableColumnValueMap();
            Map<SqlColumn, SQLInListExpr> currentTableColumnInValuesMap = parser.getCurrentTableColumnInValuesMap();

            Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> conditionTableMap = parser.getJoinConditionMap();
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
            List<SqlRefer> sqlProperties = new SqlReferParser(joinConnector.getJoinCondition(), sqlTable).getSqlReferList();
            if (sqlProperties.isEmpty()) {
                throw new SqlParseException("join的条件sqlProperties.isEmpty()");
            }
            QueryReferFilter queryReferFilter = new QueryReferFilter(logicDbConfig, sqlTable, tableOwnCondition);
            queryReferFilter.getOrderBySqlRefers().addAll(sqlProperties);
            ExecutableTableSource executableTableSource;
            if (sqlTableSource instanceof SQLExprTableSource) {
                executableTableSource =
                    new AtomicTableSource(logicDbConfig, (SQLExprTableSource) sqlTableSource, currentTableColumnValueMap, currentTableColumnInValuesMap, queryReferFilter);
            } else if (sqlTableSource instanceof SQLJoinTableSource) {
                throw new ParserException("SQLJoinTableSource 不应出现");
            } else if (sqlTableSource instanceof SQLSubqueryTableSource) {
                executableTableSource = new SubQueriedTableSource(logicDbConfig, (SQLSubqueryTableSource) sqlTableSource, queryReferFilter);
            } else if (sqlTableSource instanceof SQLUnionQueryTableSource) {
                executableTableSource = new UnionQueriedTableSource(logicDbConfig, (SQLUnionQueryTableSource) sqlTableSource, queryReferFilter);
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
            ((PartitionSqlASTVisitor) visitor).visit(this);
        } else {
            throw new SqlParseException("visitor not match");
        }
    }

    public SQLExpr getOtherCondition() {
        return otherCondition;
    }



}
