package org.the.force.jdbc.partition.engine.sql.parameter;

import org.the.force.jdbc.partition.engine.sql.SqlParameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/6.
 */
public class LongSqlParameter implements SqlParameter {

    private final long value;

    public LongSqlParameter(long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setLong(parameterIndex,value);
    }
}
