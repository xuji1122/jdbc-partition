package org.the.force.jdbc.partition.engine.executor.dql.elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/13.
 */
public class Select {

    private final boolean distinctAll;

    private List<ValueItem> valueItems = new ArrayList<>();

    private int queryBound;

    private int extendBound;

    public Select(boolean distinctAll) {
        this.distinctAll = distinctAll;
    }

    public List<ValueItem> getValueItems() {
        return valueItems;
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

    public boolean isDistinctAll() {
        return distinctAll;
    }
}
