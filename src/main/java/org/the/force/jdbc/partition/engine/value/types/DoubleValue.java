package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlDecimal;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.TypedValue;
import org.the.force.jdbc.partition.engine.value.literal.LiteralDecimal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by xuji on 2017/7/6.
 */
public class DoubleValue extends AbstractSqlValue implements SqlParameter, TypedValue, SqlDecimal {

    private final double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    public Double getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setDouble(parameterIndex, value);
    }

    public int getSqlType() {
        return Types.DOUBLE;
    }


    public BigDecimal getNumber() {
        return new BigDecimal(getValue().toString());
    }


    public DoubleValue add(SqlValue sqlValue) throws SQLException {
        return new DoubleValue(new LiteralDecimal(this.toString()).add(sqlValue).getValue().doubleValue());
    }

    public DoubleValue subtract(SqlValue sqlValue) throws SQLException {
        return new DoubleValue(new LiteralDecimal(this.toString()).subtract(sqlValue).getValue().doubleValue());
    }

    public DoubleValue multiply(SqlValue sqlValue) throws SQLException {
        return new DoubleValue(new LiteralDecimal(this.toString()).multiply(sqlValue).getValue().doubleValue());
    }

    public DoubleValue divide(SqlValue sqlValue) throws SQLException {
        return new DoubleValue(new LiteralDecimal(this.toString()).divide(sqlValue).getValue().doubleValue());
    }

    public DoubleValue mod(SqlValue sqlValue) throws SQLException {
        return new DoubleValue(new LiteralDecimal(this.toString()).mod(sqlValue).getValue().doubleValue());
    }
}
