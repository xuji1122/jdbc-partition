package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlDecimal;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlParameterFactory;
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
public class FloatValue extends AbstractSqlValue implements SqlParameter,TypedValue,SqlDecimal,SqlParameterFactory {

    private final float value;

    public FloatValue(float value) {
        this.value = value;
    }

    public Float getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setFloat(parameterIndex, value);
    }

    public int getSqlType() {
        return Types.FLOAT;
    }


    public BigDecimal getNumber() {
        return new BigDecimal(getValue().toString());
    }


    public FloatValue add(SqlValue sqlValue) throws SQLException {
        return new FloatValue(new LiteralDecimal(this.toString()).add(sqlValue).getValue().floatValue());
    }

    public FloatValue subtract(SqlValue sqlValue) throws SQLException {
        return new FloatValue(new LiteralDecimal(this.toString()).subtract(sqlValue).getValue().floatValue());
    }

    public FloatValue multiply(SqlValue sqlValue) throws SQLException {
        return new FloatValue(new LiteralDecimal(this.toString()).multiply(sqlValue).getValue().floatValue());
    }

    public FloatValue divide(SqlValue sqlValue) throws SQLException {
        return new FloatValue(new LiteralDecimal(this.toString()).divide(sqlValue).getValue().floatValue());
    }

    public FloatValue mod(SqlValue sqlValue) throws SQLException {
        return new FloatValue(new LiteralDecimal(this.toString()).mod(sqlValue).getValue().floatValue());
    }

    public SqlParameter parse(String input) {
        return new FloatValue(Float.parseFloat(input));
    }

}
