package org.the.force.jdbc.partition.engine.executor.dql.elements;

/**
 * Created by xuji on 2017/7/14.
 */
public class GroupByItem {

    private final ValueExpr valueExpr;

    public GroupByItem(ValueExpr valueExpr) {
        this.valueExpr = valueExpr;
    }

    public ValueExpr getValueExpr() {
        return valueExpr;
    }
}
