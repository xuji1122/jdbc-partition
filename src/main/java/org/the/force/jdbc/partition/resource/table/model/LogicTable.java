package org.the.force.jdbc.partition.resource.table.model;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuji on 2017/6/7.
 */
public class LogicTable {

    private final String catalog;

    private final String schema;

    private final String tableName;

    private final Map<String, LogicColumn> columns = new LinkedHashMap<>();

    private final Set<String> pkColumns = new LinkedHashSet<>();

    private final Map<String, Set<String>> uniqueColumns = new LinkedHashMap<>();

    //index信息

    public LogicTable(String catalog, String schema, String tableName, DatabaseMetaData databaseMetaData) throws Exception {
        this.catalog = catalog;
        this.schema = schema;
        this.tableName = tableName;
        init(databaseMetaData);
    }

    private void init(DatabaseMetaData databaseMetaData) throws Exception {
        ResultSet rs = databaseMetaData.getColumns(catalog, schema, tableName, null);
        while (rs.next()) {
            String columnName = rs.getString("COLUMN_NAME");
            int sqlDataType = rs.getInt("DATA_TYPE");
            LogicColumn logicColumn = new LogicColumn(this, columnName, sqlDataType);
            columns.put(columnName.toLowerCase(), logicColumn);
        }
        rs = databaseMetaData.getPrimaryKeys(catalog, schema, tableName);
        while (rs.next()) {
            String pkColumn = rs.getString(4);
            pkColumns.add(pkColumn);
        }
        rs = databaseMetaData.getIndexInfo(catalog, schema, tableName, false, true);
        while (rs.next()) {
            if (rs.getBoolean("NON_UNIQUE")) {
                continue;
            }
            String indexName = rs.getString("INDEX_NAME");
            if (!uniqueColumns.containsKey(indexName)) {
                uniqueColumns.put(indexName, new LinkedHashSet<>());
            }
            uniqueColumns.get(indexName).add(rs.getString("COLUMN_NAME"));
        }
    }

    public Map<String, LogicColumn> getColumns() {
        return columns;
    }

    public Set<String> getPkColumns() {
        return pkColumns;
    }

    public Map<String, Set<String>> getUniqueColumns() {
        return uniqueColumns;
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
        if (getSchema() != null) {
            if (!getSchema().equals(that.getSchema()))
                return false;
        } else {
            if (that.getSchema() != null) {
                return false;
            }
        }
        return getTableName().equals(that.getTableName());

    }

    public int hashCode() {
        int result = getCatalog().hashCode();
        if (getSchema() != null) {
            result = 31 * result + getSchema().hashCode();
        }
        result = 31 * result + getTableName().hashCode();
        return result;
    }

    public String toString() {
        return tableName;
    }
}
