package org.the.force.jdbc.partition.engine.executor.dql.aggregate;

import java.util.Arrays;

/**
 * Created by xuji on 2017/6/8.
 */
public class StringsRowKey{

    private final String[] value;

    public StringsRowKey(String[] value) {
        this.value = value;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        StringsRowKey that = (StringsRowKey) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(value, that.value);
    }

    public int hashCode() {
        return Arrays.hashCode(value);
    }

    public String toString() {
        return "StringsRowKey{" + "eval=" + Arrays.toString(value) + '}';
    }

    public String[] getValue() {
        return value;
    }
}
