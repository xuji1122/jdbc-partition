package org.the.force.jdbc.partition.engine.sql.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/14.
 * distinct all的时候如果依据select label之外的字段排序是无效的，将会被忽略
 * 判断的标准
 * 1，distinctAllGroupBy存在
 * 2，OrderByItem指向的列在隐藏列中
 */
public class OrderBy {


    private List<OrderByItem> orderByItems = new ArrayList<>();

    private int sortedIndexTo = 0;//


    public OrderByItem getOrderByItem(int index) {
        return orderByItems.get(index);
    }

    public int getItemSize() {
        return orderByItems == null ? 0 : orderByItems.size();
    }

    public int getSortedIndexTo() {
        return sortedIndexTo;
    }

    public void setSortedIndexTo(int sortedIndexTo) {
        this.sortedIndexTo = sortedIndexTo;
    }

    public List<OrderByItem> getOrderByItems() {

        return orderByItems;
    }
}
