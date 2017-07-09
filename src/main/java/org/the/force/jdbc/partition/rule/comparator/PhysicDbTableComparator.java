package org.the.force.jdbc.partition.rule.comparator;

import org.the.force.jdbc.partition.rule.Partition;

import java.util.Comparator;

/**
 * Created by xuji on 2017/5/14.
 */
public class PhysicDbTableComparator implements Comparator<Partition> {

    public int compare(Partition o1, Partition o2) {
        NameComparator nameComparator =  NameComparator.getSingleton();
        int c = nameComparator.compare(o1.getPhysicDbName(), o2.getPhysicDbName());
        if (c != 0) {
            return c;
        }
        return nameComparator.compare(o1.getPhysicTableName(), o2.getPhysicTableName());
    }

}
