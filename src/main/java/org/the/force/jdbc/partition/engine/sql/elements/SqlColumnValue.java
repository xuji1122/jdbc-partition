package org.the.force.jdbc.partition.engine.sql.elements;

import org.the.force.jdbc.partition.rule.PartitionColumnValue;

/**
 * Created by xuji on 2017/5/14.
 */
public class SqlColumnValue implements PartitionColumnValue {

    private final String columnName;

    private final Object value;



    public SqlColumnValue(String columnName, Object value) {
        this.columnName = columnName.toLowerCase();
        this.value = value;
    }



    public String getColumnName() {
        return columnName;
    }

    public Object getValue() {
        return value;
    }


    public final int compareTo(PartitionColumnValue o) {
        return this.getColumnName().compareTo(o.getColumnName());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SqlColumnValue that = (SqlColumnValue) o;

        return getColumnName().equals(that.getColumnName());

    }

    @Override
    public final int hashCode() {
        return getColumnName().hashCode();
    }

    public String toString() {
        return columnName + ':' + value;
    }
}
