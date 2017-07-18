package org.the.force.jdbc.partition.engine.sql;

import org.the.force.jdbc.partition.common.tuple.Pair;
import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.evaluator.row.SQLInListEvaluator;
import org.the.force.jdbc.partition.engine.sql.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.sql.SqlTable;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLBinaryOpExpr;

import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/17.
 */
public interface ConditionalSqlTable extends SqlTable {

    Map<SqlRefer, SqlExprEvaluator> getColumnValueMap();

    Map<List<SQLExpr>, SQLInListEvaluator> getColumnInValueListMap();

    Map<Pair<Integer, Integer>, List<SQLBinaryOpExpr>> getJoinConditionMap();

    SQLExpr getTableOwnCondition();

    void setTableOwnCondition(SQLExpr tableOwnCondition);

}
