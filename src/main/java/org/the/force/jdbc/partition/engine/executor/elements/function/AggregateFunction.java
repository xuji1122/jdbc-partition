package org.the.force.jdbc.partition.engine.executor.elements.function;

import org.the.force.jdbc.partition.engine.executor.elements.item.Item;
import org.the.force.jdbc.partition.engine.result.DataItemRow;

import java.util.List;

/**
 * Created by xuji on 2017/6/7.
 * 假定 sql设置的parameter已经写入到FieldValueFunction的视线中
 * FieldValueFunction的目的是根据数据库查询到的列的value生成计算value
 */
public interface AggregateFunction {

    Object getValue(int index,Item item,List<DataItemRow> rows);

}
