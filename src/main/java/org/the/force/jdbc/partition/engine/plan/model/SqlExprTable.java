package org.the.force.jdbc.partition.engine.plan.model;

import org.the.force.jdbc.partition.common.PartitionJdbcConstants;

import java.util.Set;

/**
 * Created by xuji on 2017/5/23.
 */
public class SqlExprTable implements SqlTable {
    private final String schema;
    private final String tableName;
    private String alias;

    public SqlExprTable(String schema, String tableName, String alias) {
        if (schema == null || schema.length() < 1) {
            schema = PartitionJdbcConstants.EMPTY_NAME;
        }
        this.schema = schema;
        if (tableName == null || tableName.length() < 1) {
            tableName = PartitionJdbcConstants.EMPTY_NAME;
        }
        this.tableName = tableName;
        this.alias = alias;
    }

    public String getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SqlExprTable that = (SqlExprTable) o;

        if (!getSchema().equalsIgnoreCase(that.getSchema()))
            return false;
        return getTableName().equalsIgnoreCase(that.getTableName());

    }

    public int hashCode() {
        int result = getSchema().toLowerCase().hashCode();
        result = 31 * result + getTableName().toLowerCase().hashCode();
        return result;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(schema).append(".").append(tableName);
        if (alias != null) {
            sb.append("\t").append(alias);
        }
        return sb.toString();
    }

    public int compareTo(SqlExprTable o) {
        int c = this.schema.compareTo(o.getSchema());
        if (c != 0) {
            return c;
        }
        return this.tableName.compareTo(o.getTableName());
    }

    //TODO
    public Set<String> getColumns() {
        return null;
    }
}
