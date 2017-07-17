package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/6/14.
 */
public abstract class QueriedSqlTable implements ConditionalSqlTable {

    private final String alias;

    private final SQLTableSource sqlTableSource;



    public QueriedSqlTable(SQLTableSource sqlTableSource) {
        if (sqlTableSource instanceof SQLExprTableSource) {
            throw new SqlParseException("sqlTableSource instanceof SQLExprTableSource");
        }
        this.sqlTableSource = sqlTableSource;
        this.alias = sqlTableSource.getAlias();
    }

    public String getAlias() {
        return alias;
    }

    public String getTableName() {
        return null;
    }

    public final boolean equals(Object o) {
        return sqlTableSource.equals(o);

    }

    public final int hashCode() {
        return sqlTableSource.hashCode();
    }

    public final void setAlias(String alias) {

    }

    public abstract List<String> getReferLabels();

    public SQLTableSource getSQLTableSource() {
        return sqlTableSource;
    }

    public String getRelativeKey() {
        return alias;
    }

    private final Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> joinConditionMap = new LinkedHashMap<>();

    private SQLExpr currentTableOwnCondition;//归集到currentSqlTable的sql条件


    public Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> getJoinConditionMap() {
        return joinConditionMap;
    }

    public SQLExpr getCurrentTableOwnCondition() {
        return currentTableOwnCondition;
    }

    public void setCurrentTableOwnCondition(SQLExpr currentTableOwnCondition) {
        this.currentTableOwnCondition = currentTableOwnCondition;
    }

    private final Map<SqlRefer, SqlExprEvaluator> columnValueMap = new LinkedHashMap<>();

    private final Map<List<SQLExpr>, SQLInListEvaluator> columnInValueListMap = new LinkedHashMap<>();

    public Map<SqlRefer, SqlExprEvaluator> getColumnValueMap() {
        return columnValueMap;
    }

    public Map<List<SQLExpr>, SQLInListEvaluator> getColumnInValueListMap() {
        return columnInValueListMap;
    }
}
