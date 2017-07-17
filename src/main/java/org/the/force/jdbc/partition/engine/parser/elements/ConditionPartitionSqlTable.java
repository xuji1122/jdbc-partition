package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/17.
 * update,delete select中使用的SQLExprTableSource
 * 可以根据条件分区的
 */
public class ConditionPartitionSqlTable extends ExprSqlTable implements ConditionalSqlTable{

    private final Map<SqlRefer, SqlExprEvaluator> columnValueMap = new LinkedHashMap<>();

    private final Map<List<SQLExpr>, SQLInListEvaluator> columnInValueListMap = new LinkedHashMap<>();

    private final Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> joinConditionMap = new LinkedHashMap<>();

    private SQLExpr currentTableOwnCondition;//归集到currentSqlTable的sql条件


    public ConditionPartitionSqlTable(LogicDbConfig logicDbConfig, SQLExprTableSource sqlExprTableSource) {
        super(logicDbConfig, sqlExprTableSource);
    }

    public Map<SqlRefer, SqlExprEvaluator> getColumnValueMap() {
        return columnValueMap;
    }

    public Map<List<SQLExpr>, SQLInListEvaluator> getColumnInValueListMap() {
        return columnInValueListMap;
    }

    public Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> getJoinConditionMap() {
        return joinConditionMap;
    }


    public SQLExpr getCurrentTableOwnCondition() {
        return currentTableOwnCondition;
    }

    public void setCurrentTableOwnCondition(SQLExpr currentTableOwnCondition) {
        this.currentTableOwnCondition = currentTableOwnCondition;
    }

}
