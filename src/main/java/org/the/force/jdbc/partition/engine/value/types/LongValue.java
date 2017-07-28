package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlInteger;
import org.the.force.jdbc.partition.engine.value.SqlNumber;
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
public class LongValue extends AbstractSqlValue implements SqlParameter, TypedValue, SqlInteger, SqlParameterFactory {

    private final long value;

    public LongValue(long value) {
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setLong(parameterIndex, value);
    }

    public int getSqlType() {
        return Types.BIGINT;
    }

    public LongValue clone(SqlValue sqlNumber) {
        long b = Long.parseLong(sqlNumber.toString());
        return new LongValue(b);
    }

    public BigInteger getNumber() {
        return new BigInteger(this.toString());
    }

    public LongValue add(SqlValue sqlValue) throws SQLException {
        return new LongValue(new BigIntegerValue(this.toString()).add(sqlValue).getNumber().longValue());
    }

    public LongValue subtract(SqlValue sqlValue) throws SQLException {
        return new LongValue(new BigIntegerValue(this.toString()).subtract(sqlValue).getNumber().longValue());
    }

    public LongValue multiply(SqlValue sqlValue) throws SQLException {
        return new LongValue(new BigIntegerValue(this.toString()).multiply(sqlValue).getNumber().longValue());
    }

    public LongValue divide(SqlValue sqlValue) throws SQLException {
        return new LongValue(new BigIntegerValue(this.toString()).divide(sqlValue).getNumber().longValue());
    }

    public LongValue mod(SqlValue sqlValue) throws SQLException {
        return new LongValue(new BigIntegerValue(this.toString()).mod(sqlValue).getNumber().longValue());
    }

    public SqlParameter parse(String input) {
        return new LongValue(Long.parseLong(input));
    }
}
