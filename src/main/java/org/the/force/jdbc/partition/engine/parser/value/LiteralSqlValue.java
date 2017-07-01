package org.the.force.jdbc.partition.engine.parser.value;

/**
 * Created by xuji on 2017/5/20.
 */
public class LiteralSqlValue implements SqlValue {

    private final Object value;

    public LiteralSqlValue(Object value) {
        this.value = value;
    }
    public Object getValue() {
        return value;
    }

}
