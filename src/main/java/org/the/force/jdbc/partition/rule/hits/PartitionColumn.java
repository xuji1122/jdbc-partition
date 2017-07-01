package org.the.force.jdbc.partition.rule.hits;

/**
 * Created by xuji on 2017/5/27.
 */
public class PartitionColumn {

    private final String logicTableName;

    private final String columnName;

    public PartitionColumn(String logicTableName, String columnName) {
        this.logicTableName = logicTableName;
        this.columnName = columnName;
    }


    public String getLogicTableName() {
        return logicTableName;
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

        PartitionColumn that = (PartitionColumn) o;

        if (!getLogicTableName().equals(that.getLogicTableName()))
            return false;
        return getColumnName().equals(that.getColumnName());

    }

    @Override
    public int hashCode() {
        int result = getLogicTableName().hashCode();
        result = 31 * result + getColumnName().hashCode();
        return result;
    }

    @Override
    public String toString() {
        return logicTableName + '.' + columnName;
    }
}
