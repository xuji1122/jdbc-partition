package org.the.force.jdbc.partition.engine.sql.query;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.thirdparty.druid.sql.ast.SQLOrderingSpecification;

/**
 * Created by xuji on 2017/7/26.
 */
public class GroupByItem {

    private SqlExprEvaluator itemExprEvaluator;

    private SQLOrderingSpecification sqlOrderingSpecification = SQLOrderingSpecification.ASC;

    public GroupByItem(SqlExprEvaluator itemExprEvaluator) {
        this.itemExprEvaluator = itemExprEvaluator;
    }

    public SqlExprEvaluator getItemExprEvaluator() {
        return itemExprEvaluator;
    }

    public void setItemExprEvaluator(SqlExprEvaluator itemExprEvaluator) {
        this.itemExprEvaluator = itemExprEvaluator;
    }

    public SQLOrderingSpecification getSqlOrderingSpecification() {
        return sqlOrderingSpecification;
    }

    public void setSqlOrderingSpecification(SQLOrderingSpecification sqlOrderingSpecification) {
        this.sqlOrderingSpecification = sqlOrderingSpecification;
    }
}
