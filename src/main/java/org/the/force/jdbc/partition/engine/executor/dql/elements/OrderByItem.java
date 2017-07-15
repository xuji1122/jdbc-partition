package org.the.force.jdbc.partition.engine.executor.dql.elements;

/**
 * Created by xuji on 2017/7/14.
 */
public class OrderByItem {

    private final ValueExprItem valueExprItem;

    private final boolean asc;

    public OrderByItem(ValueExprItem valueExprItem, boolean asc) {
        this.valueExprItem = valueExprItem;
        this.asc = asc;
    }

    public ValueExprItem getValueExprItem() {
        return valueExprItem;
    }

    public boolean isAsc() {
        return asc;
    }
}
