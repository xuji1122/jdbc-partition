package org.the.force.jdbc.partition.resource.table.model;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by xuji on 2017/6/7.
 */
public class LogicTable {

    private final String catalog;

    private final String schema;

    private final String tableName;

    private final Map<String, LogicColumn> columns = new LinkedHashMap<>();

    private final Map<String, LogicColumn> pkColumns = new LinkedHashMap<>();

    private final Map<String, Set<LogicColumn>> uniqueColumns = new LinkedHashMap<>();

    //index信息

    public LogicTable(String catalog, String schema, String tableName, DatabaseMetaData databaseMetaData) throws SQLException {
        this.catalog = catalog;
        this.schema = schema;
        this.tableName = tableName;
        init(databaseMetaData);
    }

    private void init(DatabaseMetaData databaseMetaData) throws SQLException {
        ResultSet rs = databaseMetaData.getColumns(catalog, schema, tableName, null);
        while (rs.next()) {
            String columnName = rs.getString("COLUMN_NAME");
            int sqlDataType = rs.getInt("DATA_TYPE");
            LogicColumn logicColumn = new LogicColumn(this, columnName, sqlDataType);
            columns.put(columnName.toLowerCase(), logicColumn);
        }
        rs = databaseMetaData.getPrimaryKeys(catalog, schema, tableName);
        while (rs.next()) {
            String pkColumn = rs.getString("COLUMN_NAME").toLowerCase();
            pkColumns.put(pkColumn, columns.get(pkColumn));
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
            uniqueColumns.get(indexName).add(columns.get(rs.getString("COLUMN_NAME").toLowerCase()));
        }
    }

    public List<String> getColumns() {

        return columns.values().stream().map(LogicColumn::getColumnName).collect(Collectors.toList());
    }

    public List<String> getPkColumns() {
        return pkColumns.values().stream().map(LogicColumn::getColumnName).collect(Collectors.toList());
    }

    public Map<String, Set<LogicColumn>> getUniqueColumns() {
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
