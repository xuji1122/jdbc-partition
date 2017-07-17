package org.the.force.jdbc.partition.engine.executor.result;

import org.the.force.thirdparty.druid.sql.ast.SQLOrderingSpecification;

/**
 * Created by xuji on 2017/6/6.
 */
public class OrderByItem {
    private final int itemIndex;//0开始
    private final SQLOrderingSpecification orderByType;

    public OrderByItem(int itemIndex, SQLOrderingSpecification orderByType) {
        this.itemIndex = itemIndex;
        this.orderByType = orderByType;
    }

    public int getItemIndex() {
        return itemIndex;
    }

    public SQLOrderingSpecification getOrderByType() {
        return orderByType;
    }


}
