package org.the.force.jdbc.partition.engine.sql.query;

import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/13.
 */
public class Select {

    //被引用的立场
    private final ConditionalSqlTable sqlTable;

    private final boolean distinctAll;

    private List<ValueExprItem> valueExprItems = new ArrayList<>();

    private int queryBound;

    private int extendBound;

    public Select(ConditionalSqlTable sqlTable,boolean distinctAll) {
        this.sqlTable = sqlTable;
        this.distinctAll = distinctAll;
    }

    public List<ValueExprItem> getValueExprItems() {
        return valueExprItems;
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



}
