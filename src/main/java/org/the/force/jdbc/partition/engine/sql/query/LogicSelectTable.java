package org.the.force.jdbc.partition.engine.sql.query;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;

/**
 * Created by xuji on 2017/7/22.
 * 对应于{@link org.the.force.jdbc.partition.engine.executor.dql.logic.LogicBlockQueryExecutor}
 * 的查询结果集的table结构
 * 因为是逻辑的关系，因此实现方式是自由的，没有{@link PartitionSelectTable}那么多约定
 */
public class LogicSelectTable extends SelectTable{

    private SqlExprEvaluator condition;

    public LogicSelectTable(ConditionalSqlTable sqlTable, boolean distinctAll) {
        super(sqlTable, distinctAll);
    }

    public SqlExprEvaluator getCondition() {
        return condition;
    }

    public void setCondition(SqlExprEvaluator condition) {
        this.condition = condition;
    }


}
