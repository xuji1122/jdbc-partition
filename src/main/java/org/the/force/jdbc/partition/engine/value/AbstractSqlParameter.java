package org.the.force.jdbc.partition.engine.value;

/**
 * Created by xuji on 2017/7/21.
 */
public abstract class AbstractSqlParameter extends AbstractSqlValue implements SqlParameter {

    public Object getValue() {
        throw new UnsupportedOperationException("getValue:" + this.getClass().getName());
    }

}
