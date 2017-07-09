package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.jdbc.partition.rule.Partition;

/**
 * Created by xuji on 2017/5/24.
 */
public class SqlTablePartition {

    private final ExprSqlTable exprSqlTable;
    //分库分表的结果
    private final Partition partition;

    public SqlTablePartition(ExprSqlTable exprSqlTable, Partition partition) {
        this.exprSqlTable = exprSqlTable;
        this.partition = partition;
    }

    public ExprSqlTable getExprSqlTable() {
        return exprSqlTable;
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
        return "SqlTablePartition{" + "exprSqlTable=" + exprSqlTable + ", partition=" + partition + '}';
    }
}
