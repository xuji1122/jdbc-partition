package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlInteger;
import org.the.force.jdbc.partition.engine.value.SqlNumber;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlParameterFactory;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.literal.BigIntegerValue;

import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by xuji on 2017/7/6.
 */
public class ShortValue extends AbstractSqlValue implements SqlParameter,SqlInteger,SqlParameterFactory {

    private final short value;

    public ShortValue(short value) {
        this.value = value;
    }

    public Short getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setShort(parameterIndex, value);
    }

    public int getSqlType() {
        return Types.SMALLINT;
    }

    public SqlNumber clone(SqlNumber sqlNumber) {
        short b = Short.parseShort(sqlNumber.toString());
        return new ShortValue(b);
    }

    public BigInteger getNumber() {
        return new BigInteger(this.toString());
    }

    public ShortValue add(SqlValue sqlValue) throws SQLException {
        return new ShortValue(new BigIntegerValue(this.toString()).add(sqlValue).getNumber().shortValue());
    }

    public ShortValue subtract(SqlValue sqlValue) throws SQLException {
        return new ShortValue(new BigIntegerValue(this.toString()).subtract(sqlValue).getNumber().shortValue());
    }

    public ShortValue multiply(SqlValue sqlValue) throws SQLException {
        return new ShortValue(new BigIntegerValue(this.toString()).multiply(sqlValue).getNumber().shortValue());
    }

    public ShortValue divide(SqlValue sqlValue) throws SQLException {
        return new ShortValue(new BigIntegerValue(this.toString()).divide(sqlValue).getNumber().shortValue());
    }

    public ShortValue mod(SqlValue sqlValue) throws SQLException {
        return new ShortValue(new BigIntegerValue(this.toString()).mod(sqlValue).getNumber().shortValue());
    }

    public SqlParameter parse(String input) {
        return new ShortValue(Short.parseShort(input));
    }

}
