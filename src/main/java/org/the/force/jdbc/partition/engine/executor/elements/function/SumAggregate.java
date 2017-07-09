package org.the.force.jdbc.partition.engine.executor.elements.function;

import org.the.force.jdbc.partition.engine.executor.elements.item.Item;
import org.the.force.jdbc.partition.engine.result.DataItemRow;

import java.util.List;

/**
 * Created by xuji on 2017/6/6.
 */
public class SumAggregate implements AggregateFunction {

    @Override
    public Object getValue(int index, Item item, List<DataItemRow> rows) {
        return null;
    }
}
