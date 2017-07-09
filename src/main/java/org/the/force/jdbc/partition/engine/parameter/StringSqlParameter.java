package org.the.force.jdbc.partition.engine.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/6.
 */
public class StringSqlParameter implements SqlParameter {

    private final String parameter;

    public StringSqlParameter(String parameter) {
        this.parameter = parameter;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(parameterIndex, parameter);
    }

    public Object getValue() {
        return parameter;
    }


}
