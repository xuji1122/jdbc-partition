package org.the.force.jdbc.partition.engine.sql;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/17.
 */
public interface ConditionalSqlTable extends SqlTable {

    Map<SqlRefer, List<SqlExprEvaluator>> getColumnConditionsMap();

    Map<List<SQLExpr>, SQLInListEvaluator> getColumnInListConditionMap();

    /**
     * 在join的条件中指明的与其他的sqlTable的某个字段相等
     * 两个表具有此种关联时，可以共用单表的条件
     * @return
     */
    Map<SqlRefer, List<Pair<ConditionalSqlTable,SqlRefer>>> getEqualReferMap();

    SQLExpr getTableOwnCondition();

    void setTableOwnCondition(SQLExpr tableOwnCondition);

}
