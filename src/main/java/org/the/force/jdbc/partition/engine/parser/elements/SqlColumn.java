package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.jdbc.partition.common.PartitionJdbcConstants;

/**
 * Created by xuji on 2017/5/23.
 */
public class SqlColumn {
    private final SqlTable sqlTable;
    private final String columnName;

    public SqlColumn(SqlTable sqlTable, String columnName) {
        if (sqlTable == null) {
            sqlTable = PartitionJdbcConstants.EMPTY_TABLE;
        }
        this.sqlTable = sqlTable;
        this.columnName = columnName;
    }

    public SqlTable getSqlTable() {
        return sqlTable;
    }

    public String getColumnName() {
        return columnName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SqlColumn sqlColumn = (SqlColumn) o;

        if (!getSqlTable().equals(sqlColumn.getSqlTable()))
            return false;
        return getColumnName().equalsIgnoreCase(sqlColumn.getColumnName());

    }

    @Override
    public int hashCode() {
        int result = getSqlTable().hashCode();
        result = 31 * result + getColumnName().toLowerCase().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return columnName;
    }
}
