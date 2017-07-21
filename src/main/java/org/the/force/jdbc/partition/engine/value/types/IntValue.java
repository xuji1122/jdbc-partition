package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlInteger;
import org.the.force.jdbc.partition.engine.value.SqlNumber;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.TypedValue;
import org.the.force.jdbc.partition.engine.value.literal.BigIntegerValue;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by xuji on 2017/7/6.
 */
public class IntValue extends AbstractSqlValue implements SqlParameter, TypedValue, SqlInteger {

    private final int value;

    public IntValue(int value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setInt(parameterIndex, value);
    }

    public int getSqlType() {
        return Types.INTEGER;
    }

    public IntValue clone(SqlValue sqlNumber) {
        int b = Integer.parseInt(sqlNumber.toString());
        return new IntValue(b);
    }

    public BigInteger getNumber() {
        return new BigInteger(this.toString());
    }

    public IntValue add(SqlValue sqlValue) throws SQLException {
        return new IntValue(new BigIntegerValue(this.toString()).add(sqlValue).getNumber().intValue());
    }

    public IntValue subtract(SqlValue sqlValue) throws SQLException {
        return new IntValue(new BigIntegerValue(this.toString()).subtract(sqlValue).getNumber().intValue());
    }

    public IntValue multiply(SqlValue sqlValue) throws SQLException {
        return new IntValue(new BigIntegerValue(this.toString()).multiply(sqlValue).getNumber().intValue());
    }

    public IntValue divide(SqlValue sqlValue) throws SQLException {
        return new IntValue(new BigIntegerValue(this.toString()).divide(sqlValue).getNumber().intValue());
    }

    public IntValue mod(SqlValue sqlValue) throws SQLException {
        return new IntValue(new BigIntegerValue(this.toString()).mod(sqlValue).getNumber().intValue());
    }
}
