package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlInteger;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlParameterFactory;
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
public class ByteValue extends AbstractSqlValue implements SqlParameter, TypedValue, SqlInteger,SqlParameterFactory {

    private final byte value;

    public ByteValue(byte value) {
        this.value = value;
    }

    public Byte getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setByte(parameterIndex, value);
    }

    public int getSqlType() {
        return Types.TINYINT;
    }

    public BigInteger getNumber() {
        return new BigInteger(getValue().toString());
    }

    public ByteValue add(SqlValue sqlValue) throws SQLException {
        return new ByteValue(new BigIntegerValue(this.toString()).add(sqlValue).getNumber().byteValue());
    }

    public ByteValue subtract(SqlValue sqlValue) throws SQLException {
        return new ByteValue(new BigIntegerValue(this.toString()).subtract(sqlValue).getNumber().byteValue());
    }

    public ByteValue multiply(SqlValue sqlValue) throws SQLException {
        return new ByteValue(new BigIntegerValue(this.toString()).multiply(sqlValue).getNumber().byteValue());
    }

    public ByteValue divide(SqlValue sqlValue) throws SQLException {
        return new ByteValue(new BigIntegerValue(this.toString()).divide(sqlValue).getNumber().byteValue());
    }

    public ByteValue mod(SqlValue sqlValue) throws SQLException {
        return new ByteValue(new BigIntegerValue(this.toString()).mod(sqlValue).getNumber().byteValue());
    }

    public SqlParameter parse(String input) {
        return new ByteValue(Byte.parseByte(input));
    }
}
