package org.the.force.jdbc.partition.resource.table.model;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by xuji on 2017/6/7.
 */
public class LogicTable {

    private final String catalog;

    private final String schema;

    private final String tableName;

    private final Set<LogicColumn> columns = new LinkedHashSet<>();

    private final Set<String> pkColumns = new LinkedHashSet<>();

    //index信息

    public LogicTable(String schema, String tableName) {
        catalog = schema;
        this.schema = schema;
        this.tableName = tableName;
    }

    public String getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }

    public String getCatalog() {
        return catalog;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LogicTable that = (LogicTable) o;

        if (!getCatalog().equals(that.getCatalog()))
            return false;
        if (!getSchema().equals(that.getSchema()))
            return false;
        return getTableName().equals(that.getTableName());

    }

    public int hashCode() {
        int result = getCatalog().hashCode();
        result = 31 * result + getSchema().hashCode();
        result = 31 * result + getTableName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "LogicTable{" + "catalog='" + catalog + '\'' + ", schema='" + schema + '\'' + ", tableName='" + tableName + '\'' + '}';
    }
}
