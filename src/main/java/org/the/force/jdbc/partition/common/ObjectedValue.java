package org.the.force.jdbc.partition.common;

/**
 * Created by xuji on 2017/6/5.
 */
public class ObjectedValue<T> {

    private T value;


    public ObjectedValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ObjectedValue that = (ObjectedValue) o;

        return getValue().equals(that.getValue());

    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
