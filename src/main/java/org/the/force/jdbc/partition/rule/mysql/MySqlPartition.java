package org.the.force.jdbc.partition.rule.mysql;

import org.the.force.jdbc.partition.rule.Partition;

/**
 * Created by xuji on 2017/6/30.
 */
public abstract class MySqlPartition implements Partition {

    private final String physicTableName;

    private final String physicDbName;//物理表对应的物理库名

    public MySqlPartition(String physicTableName, String physicDbName) {
        this.physicTableName = physicTableName;
        this.physicDbName = physicDbName;
    }

    public final String getPhysicTableName() {
        return physicTableName;
    }

    public final String getPhysicDbName() {
        return physicDbName;
    }

    public final int hashCode() {
        int result = getPhysicTableName().hashCode();
        result = 31 * result + getPhysicDbName().hashCode();
        return result;
    }

    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || !(o instanceof Partition))
            return false;

        Partition that = (Partition) o;

        if (!getPhysicTableName().equals(that.getPhysicTableName()))
            return false;
        return getPhysicDbName().equals(that.getPhysicDbName());
    }

    public String toString() {
        return physicDbName + "." + physicTableName;
    }
}
