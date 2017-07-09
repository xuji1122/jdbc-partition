package org.the.force.jdbc.partition.engine.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/6.
 */
public class IntegerSqlParameter implements SqlParameter{

    private final int value;

    public IntegerSqlParameter(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(parameterIndex,value);
    }
}
