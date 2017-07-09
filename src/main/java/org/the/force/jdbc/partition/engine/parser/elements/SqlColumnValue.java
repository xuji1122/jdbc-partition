package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.jdbc.partition.rule.PartitionColumnValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/5/14.
 */
public class SqlColumnValue implements PartitionColumnValue {

    private final String columnName;

    private List<Object> values = new ArrayList<>();

    private int currentIndex = -1;


    public SqlColumnValue(String columnName) {
        this.columnName = columnName;
    }



    public SqlColumnValue(String columnName, Object value) {
        this.columnName = columnName.toLowerCase();
        setValue(value);
    }



    public String getColumnName() {
        return columnName;
    }

    public Object getValue() {
        if (values.isEmpty()) {
            return null;
        }
        return values.get(currentIndex);
    }

    public void setValue(Object value) {
        values.clear();
        values.add(value);
        currentIndex = 0;
    }

    public void addValue(Object value) {
        values.add(value);
    }

    public void addIndex() {
        currentIndex++;
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


    public List<Object> getValues() {
        return values;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public String toString() {
        return columnName + ':' + values;
    }
}
