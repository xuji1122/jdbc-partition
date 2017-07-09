package org.the.force.jdbc.partition.rule;

/**
 * Created by xuji on 2017/5/21.
 */
public interface Partition {

    String getPhysicTableName();

    String getPhysicDbName();

    int hashCode();

    boolean equals(Object o);
}
