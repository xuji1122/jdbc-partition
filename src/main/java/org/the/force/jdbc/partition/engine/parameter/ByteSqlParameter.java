package org.the.force.jdbc.partition.engine.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/6.
 */
public class ByteSqlParameter implements SqlParameter{

    private final byte value;

    public ByteSqlParameter(byte value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setByte(parameterIndex,value);
    }
}
