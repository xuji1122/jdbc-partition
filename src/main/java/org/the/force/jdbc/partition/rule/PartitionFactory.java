package org.the.force.jdbc.partition.rule;

import org.the.force.jdbc.partition.config.DataNode;

/**
 * Created by xuji on 2017/6/30.
 */
public interface PartitionFactory {

    Partition buildPartition(DataNode partitionNode) throws Exception;


}
