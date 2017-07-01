package org.the.force.jdbc.partition.rule;

/**
 * Created by xuji on 2017/5/20.
 */
public interface PartitionColumnValue extends Comparable<PartitionColumnValue> {

    String getColumnName();

    Object getValue();


}
