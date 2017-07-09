package org.the.force.jdbc.partition.engine.parameter;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/6.
 */
public class DecimalSqlParameter implements SqlParameter{

    private final BigDecimal value;

    public DecimalSqlParameter(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setBigDecimal(parameterIndex,value);
    }
}
