package org.the.force.jdbc.partition.engine.executor.query.value;

import org.the.force.jdbc.partition.engine.result.DataItemRow;

import java.util.List;

/**
 * Created by xuji on 2017/7/13.
 * 从result set的多行结果汇总简单merge value
 */
public interface SelfAggregateFunction {

    Object getValue(List<DataItemRow> rows);

}
