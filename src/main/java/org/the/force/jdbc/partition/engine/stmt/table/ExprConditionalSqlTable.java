package org.the.force.jdbc.partition.engine.stmt.table;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.jdbc.partition.engine.stmt.ConditionalSqlTable;
import org.the.force.jdbc.partition.engine.stmt.SqlRefer;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
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

    private final Map<SqlRefer, List<SqlExprEvaluator>> columnConditionsMap = new LinkedHashMap<>();

    private final Map<List<SQLExpr>, SQLInListEvaluator> columnInListConditionMap = new LinkedHashMap<>();

    private final Map<SqlRefer,List<Pair<ConditionalSqlTable,SqlRefer>>> equalReferMap = new LinkedHashMap<>();

    private SQLExpr tableOwnCondition;//归集到currentSqlTable的sql条件


    public ExprConditionalSqlTable(LogicDbConfig logicDbConfig, SQLExprTableSource sqlExprTableSource) {
        super(logicDbConfig, sqlExprTableSource);
    }

    public Map<SqlRefer, List<SqlExprEvaluator>> getColumnConditionsMap() {
        return columnConditionsMap;
    }

    public Map<List<SQLExpr>, SQLInListEvaluator> getColumnInListConditionMap() {
        return columnInListConditionMap;
    }

    public Map<SqlRefer,List<Pair<ConditionalSqlTable,SqlRefer>>> getEqualReferMap() {
        return equalReferMap;
    }


    public SQLExpr getTableOwnCondition() {
        return tableOwnCondition;
    }

    public void setTableOwnCondition(SQLExpr tableOwnCondition) {
        this.tableOwnCondition = tableOwnCondition;
    }

}
