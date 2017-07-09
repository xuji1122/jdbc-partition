package org.the.force.jdbc.partition.engine.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/6.
 */
public class DoubleSqlParameter implements SqlParameter{

    private final double value;

    public DoubleSqlParameter(double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setDouble(parameterIndex,value);
    }
}
