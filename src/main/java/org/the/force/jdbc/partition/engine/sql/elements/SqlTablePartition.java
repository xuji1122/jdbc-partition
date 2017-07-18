package org.the.force.jdbc.partition.engine.sql.elements;

import org.the.force.jdbc.partition.engine.sql.elements.table.ExprSqlTable;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLInsertStatement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/5/24.
 */
public class SqlTablePartition {

    private final ExprSqlTable exprSqlTable;

    //分库分表的结果
    private final Partition partition;


    private Map<SQLInListExpr, List<Object[]>> subInListExpr;//in values 路由的结果

    private List<SQLInsertStatement.ValuesClause> valuesClauses;//insert 的 values 路由结果

    private int totalPartitions;//总共有多少个分区，用于输出SQLLimit

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

    public Map<SQLInListExpr, List<Object[]>> getSubInListExpr() {
        if (subInListExpr == null) {
            subInListExpr = new LinkedHashMap<>();
        }
        return subInListExpr;
    }

    public List<SQLInsertStatement.ValuesClause> getValuesClauses() {
        if (valuesClauses == null) {
            valuesClauses = new ArrayList<>();
        }
        return valuesClauses;
    }

    public int getTotalPartitions() {
        return totalPartitions;
    }

    public void setTotalPartitions(int totalPartitions) {
        this.totalPartitions = totalPartitions;
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
