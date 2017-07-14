package org.the.force.jdbc.partition.engine.executor.query.elements;

import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/7/13.
 */
public class Select {

    private final boolean distinctAll;

    private List<SelectItem> selectItems = new ArrayList<>();

    private Map<SqlTable, List<Integer>> tableItemsMap = new LinkedHashMap<>();//null代表*  empty会有代表无

    private int queryBound;

    private int extendBound;

    public Select(boolean distinctAll) {
        this.distinctAll = distinctAll;
    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }

    public List<Integer> getSqlTableIndexes(SqlTable sqlTable) {
        if (!tableItemsMap.containsKey(sqlTable)) {
            tableItemsMap.put(sqlTable, new ArrayList<>());
        }
        return tableItemsMap.get(sqlTable);
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
}
