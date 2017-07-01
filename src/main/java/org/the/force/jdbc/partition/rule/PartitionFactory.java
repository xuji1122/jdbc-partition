package org.the.force.jdbc.partition.rule;

import org.the.force.jdbc.partition.rule.config.DataNode;

/**
 * Created by xuji on 2017/6/30.
 */
public interface PartitionFactory {

    Partition buildPartition(PartitionRule.RuleType partitionType, DataNode partitionNode) throws Exception;


}
