package org.the.force.jdbc.partition.engine.sql.parameter;

import org.the.force.jdbc.partition.engine.sql.SqlParameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/6.
 */
public class FloatSqlParameter implements SqlParameter {

    private final float value;

    public FloatSqlParameter(float value) {
        this.value = value;
    }

    public Float getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setFloat(parameterIndex, value);
    }
}
