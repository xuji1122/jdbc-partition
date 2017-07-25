package org.the.force.jdbc.partition.engine.sql.query;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/14.
 * 排序遍历的约定
 * 使用cursor的模式,随着游标下移，之前的数据将会销毁
 * 多个分区或者多个taleSource会产生多个resultSet  对外提供的resultSet接口就一个
 * 遍历游标的方式
 * 1，如果底层的顺序和目标的顺序一致，那么使用二路归并的方式遍历多个结果集，获取最值作为next并缓存中间值
 * 2，如果游标的顺序和预期的不一致，那么遍历游标时使用插入排序的方式，缓存中间结果的同时实现排序功能
 */
public class OrderBy {


    private List<OrderByItem> orderByItems;

    private int sortedIndexFrom = 0;//




    public List<OrderByItem> getOrderByItems() {
        if (orderByItems == null) {
            orderByItems = new ArrayList<>();
        }
        return orderByItems;
    }



    public int getSortedIndexFrom() {
        return sortedIndexFrom;
    }

    public void setSortedIndexFrom(int sortedIndexFrom) {
        this.sortedIndexFrom = sortedIndexFrom;
    }
}
