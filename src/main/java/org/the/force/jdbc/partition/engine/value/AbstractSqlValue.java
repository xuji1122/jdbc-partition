package org.the.force.jdbc.partition.engine.value;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/20.
 * 所有的sqlValue都应该继承AbstractSqlValue
 */
public abstract class AbstractSqlValue implements SqlValue, Comparable<SqlValue> {

    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null) {
            return false;
        }
        if (!(o instanceof SqlValue)) {
            return false;
        }
        if (this instanceof SqlNull || o instanceof SqlNull) {
            return false;
        }
        return ((SqlValue) o).getValue().equals(getValue());
    }

    public final int hashCode() {
        return getValue().hashCode();
    }

    public final int compareTo(SqlValue o) {
        return ((Comparable<Object>) this.getValue()).compareTo((Comparable<Object>) o.getValue());
    }

    public final String toString() {
        Object obj = getValue();
        if (obj == null || obj == NULL) {
            return "null";
        }
        return obj.toString();
    }

    public SqlValue add(SqlValue sqlValue) throws SQLException {
        //TODO
        throw new RuntimeException("");

    }

    public SqlValue subtract(SqlValue sqlValue) throws SQLException {
        //TODO
        throw new RuntimeException("");
    }

    public SqlValue multiply(SqlValue sqlValue) throws SQLException {
        //TODO
        throw new RuntimeException("");
    }

    public SqlValue divide(SqlValue sqlValue) throws SQLException {
        //TODO
        throw new RuntimeException("");
    }

    public SqlValue mod(SqlValue sqlValue) throws SQLException {
        //TODO
        throw new RuntimeException("");
    }
}
