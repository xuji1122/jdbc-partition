package org.the.force.jdbc.partition.rule.mysql;

import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionComparator;

/**
 * Created by xuji on 2017/5/14.
 */
public final class PhysicTablePartition extends MySqlPartition {


    PhysicTablePartition(String physicTableName, String physicDbName) {
        super(physicTableName, physicDbName);
    }

    public int compareTo(Partition o) {
        return PartitionComparator.getSingleton().compare(getPhysicTableName(), o.getPhysicTableName());
    }



}
