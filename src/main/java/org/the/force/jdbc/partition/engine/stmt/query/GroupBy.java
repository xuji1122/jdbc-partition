package org.the.force.jdbc.partition.engine.stmt.query;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by xuji on 2017/7/14.
 */
public class GroupBy {

    private SqlExprEvaluator having;//返回true false

    private final List<GroupByItem> groupByItems = new ArrayList<>();

    private int sortedIndexTo = 0;//

    public SqlExprEvaluator getHaving() {
        return having;
    }

    public void setHaving(SqlExprEvaluator having) {
        this.having = having;
    }

    public void addItem(GroupByItem groupByItem) {
        groupByItems.add(groupByItem);
    }

    public void updateSqlExprEvaluator(int index, GroupByItem groupByItem) {
        if (!groupByItem.getItemExprEvaluator().getOriginalSqlExpr().equals(groupByItems.get(index).getItemExprEvaluator().getOriginalSqlExpr())) {
            throw new RuntimeException("!sqlExprEvaluator.getOriginalSqlExpr().equals(groupByItems.get(index))");
        }
        groupByItems.set(index, groupByItem);
    }

    public GroupByItem getGroupByItem(int index) {
        return groupByItems.get(index);
    }


    public int getItemSize() {
        return groupByItems.size();
    }


    /**
     * @param groupBy
     */
    public void sortGroupByExprFrom(SelectTable selectTable, GroupBy groupBy) {
        if (groupBy == null) {
            return;
        }
        Collections.sort(groupByItems, (o1, o2) -> {
            int size = groupBy.getItemSize();
            int o1P = size;
            int o2P = size;
            for (int i = 0; i < size; i++) {
                GroupByItem groupByItem =groupBy.getGroupByItem(i);
                if (o1P == size && selectTable.checkEquals(o1.getItemExprEvaluator(), groupByItem.getItemExprEvaluator())) {
                    o1P = i;
                }
                if (o2P == size && selectTable.checkEquals(o2.getItemExprEvaluator(), groupByItem.getItemExprEvaluator())) {
                    o2P = i;
                }
            }
            return o1P - o2P;
        });
    }

    /**
     * 当使用group by重新排序时，先把group by的列尽量按照order by的列的顺序排序，让order by最大限度被兼容
     * @param orderBy
     */
    public void sortGroupByExprFrom(SelectTable selectTable, OrderBy orderBy) {
        if (orderBy == null) {
            return;
        }
        List<OrderByItem> orderByItemList = orderBy.getOrderByItems();
        Collections.sort(groupByItems, (o1, o2) -> {
            int size = orderByItemList.size();
            int o1P = size;
            int o2P = size;
            for (int i = 0; i < size; i++) {
                OrderByItem orderByItem = orderByItemList.get(i);
                if (o1P == size && selectTable.checkEquals(o1.getItemExprEvaluator(), orderByItem.getRsIndexEvaluator())) {
                    o1P = i;
                }
                if (o2P == size && selectTable.checkEquals(o2.getItemExprEvaluator(), orderByItem.getRsIndexEvaluator())) {
                    o2P = i;
                }
            }
            return o1P - o2P;
        });
    }

    public int getSortedIndexTo() {
        return sortedIndexTo;
    }

    public void setSortedIndexTo(int sortedIndexTo) {
        this.sortedIndexTo = sortedIndexTo;
    }
}
