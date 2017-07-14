package org.the.force.jdbc.partition.engine.executor.dql.elements;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/14.
 */
public class GroupBy {

    private SQLExpr having;

    private List<GroupByItem> itemList;

    public List<GroupByItem> getItemList() {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }
        return itemList;
    }
}
