package org.the.force.jdbc.partition.engine.executor.query.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/14.
 */
public class OrderBy {

    private List<OrderByItem> orderByItems;

    public List<OrderByItem> getOrderByItems() {
        if (orderByItems == null) {
            orderByItems = new ArrayList<>();
        }
        return orderByItems;
    }
}
