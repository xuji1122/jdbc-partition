package org.the.force.jdbc.partition.engine.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/6.
 */
public class ShortSqlParameter implements SqlParameter{

    private final short value;

    public ShortSqlParameter(short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setShort(parameterIndex,value);
    }
}
