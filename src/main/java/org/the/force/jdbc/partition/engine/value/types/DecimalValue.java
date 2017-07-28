package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlDecimal;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlParameterFactory;
import org.the.force.jdbc.partition.engine.value.SqlValue;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/20.
 */
public class DecimalValue extends AbstractSqlValue implements SqlParameter,SqlDecimal, SqlParameterFactory {

    private final BigDecimal value;

    public DecimalValue(BigDecimal value) {
        this.value = value;
    }

    public DecimalValue(String text) {
        this.value = new BigDecimal(text);
    }

    public BigDecimal getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setBigDecimal(parameterIndex,getValue());
    }


    public BigDecimal getNumber() {
        return getValue();
    }

    public SqlValue add(SqlValue sqlValue) throws SQLException {
        return new DecimalValue(this.value.add(new BigDecimal(sqlValue.toString())));
    }

    public SqlValue subtract(SqlValue sqlValue) throws SQLException {
        return new DecimalValue(this.value.subtract(new BigDecimal(sqlValue.toString())));
    }

    public SqlValue multiply(SqlValue sqlValue) throws SQLException {
        return new DecimalValue(this.value.multiply(new BigDecimal(sqlValue.toString())));
    }

    public SqlValue divide(SqlValue sqlValue) throws SQLException {
        return new DecimalValue(this.value.divide(new BigDecimal(sqlValue.toString()), 8, BigDecimal.ROUND_HALF_UP));
    }

    public SqlValue mod(SqlValue sqlValue) throws SQLException {
        throw new RuntimeException("can not mod");
    }

    public SqlParameter parse(String input) {
        return new DecimalValue(input);
    }
}
