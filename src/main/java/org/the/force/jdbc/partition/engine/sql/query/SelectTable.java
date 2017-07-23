package org.the.force.jdbc.partition.engine.sql.query;

import org.the.force.jdbc.partition.engine.evaluator.SqlExprEvaluator;
import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class SelectTable {

    //被引用的立场
    private final ConditionalSqlTable sqlTable;

    private final boolean distinctAll;

    private List<SqlExprEvaluator> selectValueNodes = new ArrayList<>();

    private List<String> selectLabels = new ArrayList<>();//总是有值的，不管SQLSelectItem是否有alias

    //非allColumnItems的selectItem,包括添加进去的，最终重置到查询中的sqlItem= normalSelectItems+allColumnItems
    private List<SQLSelectItem> normalSelectItems = new ArrayList<>();
    //select * 或t.*的item
    private List<SQLSelectItem> allColumnItems = new ArrayList<>();

    /**
     * 一个labe可能映射到多个列上，映射关系
     */
    private Map<String, List<Integer>> labelMap = new LinkedHashMap<>();

    private int queryBound;

    private int extendBound;

    private GroupByFunction groupByFunction;

    private OrderByFunction orderByFunction;

    private ResultLimitFunction resultLimitFunction;

    public SelectTable(ConditionalSqlTable sqlTable, boolean distinctAll) {
        this.sqlTable = sqlTable;
        this.distinctAll = distinctAll;
    }

    public void addValueNode(String label, SqlExprEvaluator sqlExprEvaluator, SQLSelectItem sqlSelectItem) {
        selectLabels.add(label);
        selectValueNodes.add(sqlExprEvaluator);
        String key = label.toLowerCase();
        List<Integer> list = labelMap.get(key);
        if (list == null) {
            list = new ArrayList<>(1);
            labelMap.put(key, list);
        }
        list.add(selectLabels.size() - 1);
        normalSelectItems.add(sqlSelectItem);
    }

    public void updateValueNode(int index, SqlExprEvaluator sqlExprEvaluator, SQLSelectItem sqlSelectItem) {
        selectValueNodes.set(index, sqlExprEvaluator);
        normalSelectItems.set(index, sqlSelectItem);
    }



    public void addOriginalNormalSelectItem(SQLSelectItem sqlSelectItem) {
        normalSelectItems.add(sqlSelectItem);
    }

    public void addAllColumnItem(SQLSelectItem item) {
        allColumnItems.add(item);
    }

    public List<SqlExprEvaluator> getSelectValueNodes() {
        return selectValueNodes;
    }

    public int getQueryBound() {
        return queryBound;
    }

    public void setQueryBound(int queryBound) {
        this.queryBound = queryBound;
    }

    public int getExtendBound() {
        return extendBound;
    }

    public void setExtendBound(int extendBound) {
        this.extendBound = extendBound;
    }

    public ConditionalSqlTable getSqlTable() {
        return sqlTable;
    }

    public boolean isDistinctAll() {
        return distinctAll;
    }

    public List<String> getSelectLabels() {
        return selectLabels;
    }

    public GroupByFunction getGroupByFunction() {
        return groupByFunction;
    }

    public OrderByFunction getOrderByFunction() {
        return orderByFunction;
    }

    public ResultLimitFunction getResultLimitFunction() {
        return resultLimitFunction;
    }

    public void setGroupByFunction(GroupByFunction groupByFunction) {
        this.groupByFunction = groupByFunction;
    }

    public void setOrderByFunction(OrderByFunction orderByFunction) {
        this.orderByFunction = orderByFunction;
    }

    public void setResultLimitFunction(ResultLimitFunction resultLimitFunction) {
        this.resultLimitFunction = resultLimitFunction;
    }

}
