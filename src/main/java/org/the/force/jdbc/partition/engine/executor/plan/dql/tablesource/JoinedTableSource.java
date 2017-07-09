package org.the.force.jdbc.partition.engine.executor.plan.dql.tablesource;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.executor.plan.dql.PlanedTableSource;
import org.the.force.jdbc.partition.engine.parser.ParserUtils;
import org.the.force.jdbc.partition.engine.parser.TableConditionParser;
import org.the.force.jdbc.partition.engine.parser.elements.JoinConnector;
import org.the.force.jdbc.partition.engine.parser.elements.SqlColumn;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.engine.parser.sqlName.SqlTableParser;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/6/4.
 */
public class JoinedTableSource extends PlanedTableSource {

    private final List<SQLTableSource> originalTableSources = new ArrayList<>();

    private final List<SqlTable> sqlTables = new ArrayList<>();

    private final Map<Pair<Integer, Integer>, JoinConnector> joinConnectorMap = new LinkedHashMap<>();

    private final Map<Integer, PlanedTableSource> planedTableSourceMap = new HashMap<>();

    private SQLExpr newWhere;


    public JoinedTableSource(LogicDbConfig logicDbConfig, SQLJoinTableSource sqlJoinTableSource, SQLExpr originalWhere) {
        super(logicDbConfig);
        super.setParent(sqlJoinTableSource.getParent());
        this.newWhere = originalWhere;
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
        joinConnectorMap.put(pair, joinConnector);
    }

    private void parseTableCondition() {
        int size = sqlTables.size();
        for (int i = 0; i < size; i++) {
            SQLTableSource sqlTableSource = originalTableSources.get(i);
            SqlTable sqlTable = sqlTables.get(i);
            TableConditionParser parser = new TableConditionParser(logicDbConfig, this.newWhere, i, sqlTables);
            this.newWhere = parser.getOtherCondition();
            SQLExpr tableOwnCondition = parser.getCurrentTableOwnCondition();
            Map<SqlColumn, SQLExpr> currentTableColumnValueMap = parser.getCurrentTableColumnValueMap();
            Map<SqlColumn, SQLInListExpr> currentTableColumnInValuesMap = parser.getCurrentTableColumnInValuesMap();
            PlanedTableSource planedTableSource;
            if (sqlTableSource instanceof SQLExprTableSource) {
                planedTableSource = new AtomicTableSource(logicDbConfig, (SQLExprTableSource) sqlTableSource, sqlTable, tableOwnCondition, currentTableColumnValueMap,
                    currentTableColumnInValuesMap);
            } else if (sqlTableSource instanceof SQLJoinTableSource) {
                throw new ParserException("SQLJoinTableSource 不应出现");
            } else if (sqlTableSource instanceof SQLSubqueryTableSource) {
                planedTableSource = new SubQueriedTableSource(logicDbConfig, (SQLSubqueryTableSource) sqlTableSource, sqlTable, tableOwnCondition);
            } else if (sqlTableSource instanceof SQLUnionQueryTableSource) {
                planedTableSource = new UnionQueriedTableSource(logicDbConfig, (SQLUnionQueryTableSource) sqlTableSource, sqlTable, tableOwnCondition);
            } else {
                //TODO
                throw new SqlParseException("无法识别的tableSource类型" + sqlTableSource.getClass().getName());
            }

            planedTableSourceMap.put(i, planedTableSource);
            Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> conditionTableMap = parser.getJoinConditionMap();
            conditionTableMap.forEach((pair, list) -> {
                Pair<Integer, Integer> key = pair;
                List<SQLBinaryOpExpr> joinConditions = conditionTableMap.get(key);
                JoinConnector connector = joinConnectorMap.get(key);
                if ((joinConditions == null || joinConditions.isEmpty()) && connector.getJoinCondition() == null) {
                    throw new ParserException("不支持cross join");
                }
                if (joinConditions != null) {
                    for (SQLBinaryOpExpr sqlBinaryOpExpr : joinConditions) {
                        connector = joinConnectorMap.get(key);
                        SQLExpr mergeExpr = mergeLogicalCondition(connector.getJoinCondition(), sqlBinaryOpExpr);
                        joinConnectorMap.put(key, new JoinConnector(connector.getJoinType(), mergeExpr));
                    }
                }
            });


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

    }

    public SQLExpr getNewWhere() {
        return newWhere;
    }
}
