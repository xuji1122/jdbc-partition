package org.the.force.jdbc.partition.engine.executor.dql.elements;

import org.the.force.jdbc.partition.engine.executor.dql.value.SelfRowValueFunction;
import org.the.force.jdbc.partition.engine.result.DataItemRow;

/**
 * Created by xuji on 2017/7/14.
 */
public class ValueItem implements SelfRowValueFunction {

    private final int index;

    private final String label;

    public ValueItem(int index, String label) {
        this.index = index;
        this.label = label;
    }

    public int getIndex() {
        return index;
    }

    public String getLabel() {
        return label;
    }

    public Object getValue(DataItemRow rows) {
        return rows.getValue(getIndex());
    }
}
