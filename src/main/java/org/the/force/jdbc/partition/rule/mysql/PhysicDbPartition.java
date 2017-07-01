package org.the.force.jdbc.partition.rule.mysql;

import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionComparator;

/**
 * Created by xuji on 2017/5/21.
 */
public final class PhysicDbPartition extends MySqlPartition {


    PhysicDbPartition(String physicTableName, String physicDbName) {
        super(physicTableName, physicDbName);
    }

    public int compareTo(Partition o) {
        return PartitionComparator.getSingleton().compare(this.getPhysicDbName(), o.getPhysicDbName());
    }

}
