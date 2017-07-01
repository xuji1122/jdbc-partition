package org.the.force.jdbc.partition.engine.plan.model;

import org.the.force.jdbc.partition.rule.Partition;

/**
 * Created by xuji on 2017/5/24.
 */
public class SqlTablePartition {

    private final SqlExprTable sqlExprTable;
    //分库分表的结果
    private final Partition partition;

    public SqlTablePartition(SqlExprTable sqlExprTable, Partition partition) {
        this.sqlExprTable = sqlExprTable;
        this.partition = partition;
    }

    public SqlExprTable getSqlExprTable() {
        return sqlExprTable;
    }

    public Partition getPartition() {
        return partition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SqlTablePartition that = (SqlTablePartition) o;

        return getPartition().equals(that.getPartition());
    }

    @Override
    public int hashCode() {
        return getPartition().hashCode();
    }

    @Override
    public String toString() {
        return "SqlTablePartition{" + "sqlExprTable=" + sqlExprTable + ", partition=" + partition + '}';
    }
}
