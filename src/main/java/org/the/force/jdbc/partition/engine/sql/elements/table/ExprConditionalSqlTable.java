package org.the.force.jdbc.partition.engine.sql.elements.table;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.sql.elements.SqlRefer;
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
public class ExprConditionalSqlTable extends ExprSqlTable implements ConditionalSqlTable {

    private final Map<SqlRefer, SqlExprEvaluator> columnValueMap = new LinkedHashMap<>();

    private final Map<List<SQLExpr>, SQLInListEvaluator> columnInValueListMap = new LinkedHashMap<>();

    private final Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> joinConditionMap = new LinkedHashMap<>();

    private SQLExpr tableOwnCondition;//归集到currentSqlTable的sql条件


    public ExprConditionalSqlTable(LogicDbConfig logicDbConfig, SQLExprTableSource sqlExprTableSource) {
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


    public SQLExpr getTableOwnCondition() {
        return tableOwnCondition;
    }

    public void setTableOwnCondition(SQLExpr tableOwnCondition) {
        this.tableOwnCondition = tableOwnCondition;
    }

}
