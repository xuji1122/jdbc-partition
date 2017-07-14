package org.the.force.jdbc.partition.engine.executor.query.elements;

import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;

/**
 * Created by xuji on 2017/7/14.
 */
public class GroupByItem {

    private final SqlRefer sqlRefer;

    private ValueItem valueItem;

    public GroupByItem(SqlRefer sqlRefer) {
        this.sqlRefer = sqlRefer;
    }

    public SqlRefer getSqlRefer() {
        return sqlRefer;
    }

    public ValueItem getValueItem() {
        return valueItem;
    }

    public void setValueItem(ValueItem valueItem) {
        this.valueItem = valueItem;
    }
}
