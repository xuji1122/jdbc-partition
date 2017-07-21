package org.the.force.jdbc.partition.engine.sql;

import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.rule.PartitionColumnValue;

/**
 * Created by xuji on 2017/5/14.
 */
public class SqlColumnValue implements PartitionColumnValue {

    private final String columnName;

    private final SqlValue sqlValue;


    public SqlColumnValue(String columnName, SqlValue sqlValue) {
        this.columnName = columnName.toLowerCase();
        this.sqlValue = sqlValue;
    }


    public String getColumnName() {
        return columnName;
    }

    public Object getValue() {
        return sqlValue.getValue();
    }

    public SqlValue getSqlValue() {
        return sqlValue;
    }


    public final int compareTo(PartitionColumnValue o) {
        return this.getColumnName().compareTo(o.getColumnName());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if(!(o instanceof PartitionColumnValue)){
            return false;
        }
        PartitionColumnValue that = (PartitionColumnValue) o;
        return getColumnName().equals(that.getColumnName());

    }

    @Override
    public final int hashCode() {
        return getColumnName().hashCode();
    }

    public String toString() {
        return columnName + ':' + sqlValue;
    }
}
