package org.the.force.jdbc.partition.engine.executor.dql.value;

import org.the.force.jdbc.partition.engine.result.DataItemRow;

/**
 * Created by xuji on 2017/7/13.
 * 直接从result set取value
 */
public interface SelfRowValueFunction {

    Object getValue(DataItemRow rows);
}
