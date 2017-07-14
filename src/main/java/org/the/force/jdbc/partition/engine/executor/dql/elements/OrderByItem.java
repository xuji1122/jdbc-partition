package org.the.force.jdbc.partition.engine.executor.dql.elements;

import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;

/**
 * Created by xuji on 2017/7/14.
 */
public class OrderByItem {

    private final SqlRefer sqlRefer;

    private final boolean asc;

    private ValueItem valueItem;


    public OrderByItem(SqlRefer sqlRefer) {
        this(sqlRefer,false);
    }

    public OrderByItem(SqlRefer sqlRefer, boolean asc) {
        this.sqlRefer = sqlRefer;
        this.asc = asc;
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
