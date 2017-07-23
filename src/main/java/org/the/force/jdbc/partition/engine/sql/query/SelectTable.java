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

    private List<String> selectLabels = new ArrayList<>();
    /**
     * 一个labe可能映射到多个列上
     */
    private Map<String, List<Integer>> labelMap = new LinkedHashMap<>();

    private List<SQLSelectItem> originalNormalSelectItems = new ArrayList<>();

    private List<SQLSelectItem> allColumnItems = new ArrayList<>();

    private int queryBound;

    private int extendBound;

    private GroupByFunction groupByFunction;

    private OrderByFunction orderByFunction;

    private ResultLimitFunction resultLimitFunction;

    public SelectTable(ConditionalSqlTable sqlTable, boolean distinctAll) {
        this.sqlTable = sqlTable;
        this.distinctAll = distinctAll;
    }

    public void addValueNode(String label, SqlExprEvaluator sqlExprEvaluator) {
        selectLabels.add(label);
        selectValueNodes.add(sqlExprEvaluator);
        String key = label.toLowerCase();
        List<Integer> list = labelMap.get(key);
        if (list == null) {
            list = new ArrayList<>(1);
            labelMap.put(key, list);
        }
        list.add(selectLabels.size() - 1);
    }

    public void addOriginalNormalSelectItem(SQLSelectItem sqlSelectItem){
        originalNormalSelectItems.add(sqlSelectItem);
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
