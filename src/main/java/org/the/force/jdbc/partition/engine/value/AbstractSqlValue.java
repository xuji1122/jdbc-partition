package org.the.force.jdbc.partition.engine.value;

import org.the.force.jdbc.partition.exception.SqlParseException;

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
        Object thisValue = this.getValue();
        Object oValue = o.getValue();
        //按照mysql的语法null排在最前面，比任何值小
        if (this.getValue() == null) {
            return 0;
        } else if (o.getValue() == null) {
            return 0;
        }
        if (!(thisValue instanceof Comparable<?>) || !(oValue instanceof Comparable<?>)) {
            throw new SqlParseException("!(thisValue instanceof Comparable<?>) || !(oValue instanceof Comparable<?>)");
        }
        return ((Comparable<Object>) thisValue).compareTo(oValue);
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
