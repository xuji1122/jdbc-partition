package org.the.force.jdbc.partition.resource.table;

import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionColumnConfig;
import org.the.force.jdbc.partition.rule.PartitionRule;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Created by xuji on 2017/5/19.
 */
public interface LogicTableConfig {

    String getLogicTableName();

    PartitionRule getPartitionRule();

    SortedSet<String> getPartitionColumnNames();

    Map<String, Set<PartitionColumnConfig>> getPartitionColumnConfigs();

    SortedSet<Partition> getPartitions();

    SortedSet<String> getPhysicDbs();

    long getVersion();

}
