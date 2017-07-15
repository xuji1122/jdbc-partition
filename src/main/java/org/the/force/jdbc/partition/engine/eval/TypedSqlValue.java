package org.the.force.jdbc.partition.engine.eval;

/**
 * Created by xuji on 2017/7/15.
 */
public class TypedSqlValue {

    private final Object value;

    private final int sqlType;

    public TypedSqlValue(Object value, int sqlType) {
        this.value = value;
        this.sqlType = sqlType;
    }

    public Object getValue() {
        return value;
    }

    public int getSqlType() {
        return sqlType;
    }
}
