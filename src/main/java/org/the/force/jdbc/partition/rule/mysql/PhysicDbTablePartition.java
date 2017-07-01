package org.the.force.jdbc.partition.rule.mysql;

import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionComparator;

/**
 * Created by xuji on 2017/5/14.
 */
public final class PhysicDbTablePartition extends MySqlPartition {



    PhysicDbTablePartition(String physicTableName, String physicDbName) {
        super(physicTableName, physicDbName);
    }

    public int compareTo(Partition o) {
        PartitionComparator partitionComparator = PartitionComparator.getSingleton();
        int c = partitionComparator.compare(getPhysicDbName(), o.getPhysicDbName());
        if (c != 0) {
            return c;
        }
        return partitionComparator.compare(getPhysicTableName(), o.getPhysicTableName());
    }

}
