package org.the.force.jdbc.partition.engine.value.literal;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlInteger;
import org.the.force.jdbc.partition.engine.value.SqlLiteral;
import org.the.force.jdbc.partition.engine.value.SqlNumber;
import org.the.force.jdbc.partition.engine.value.SqlValue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/20.
 */
public class BigIntegerValue extends AbstractSqlValue implements SqlLiteral, SqlInteger {

    private final BigInteger value;

    public BigIntegerValue(BigInteger value) {
        this.value = value;
    }

    public BigIntegerValue(String inputText) {
        this.value = new BigInteger(inputText);
    }

    public BigInteger getValue() {
        return value;
    }

    public String toSql() {
        return " " + value.toString() + " ";
    }

    public BigIntegerValue clone(SqlValue sqlNumber) {
        return new BigIntegerValue(sqlNumber.toString());
    }

    public BigInteger getNumber() {
        return getValue();
    }

    public SqlNumber add(SqlValue sqlValue) throws SQLException {
        if (sqlValue instanceof SqlNumber) {
            return new BigIntegerValue(this.value.add(new BigInteger(sqlValue.toString())));
        } else {
            return new LiteralDecimal(new BigDecimal(this.toString())).add(sqlValue);
        }
    }

    public SqlNumber subtract(SqlValue sqlValue) throws SQLException {
        if (sqlValue instanceof SqlNumber) {
            return new BigIntegerValue(this.value.subtract(new BigInteger(sqlValue.toString())));
        } else {
            return new LiteralDecimal(new BigDecimal(this.toString())).subtract(sqlValue);
        }
    }

    public SqlNumber multiply(SqlValue sqlValue) throws SQLException {
        if (sqlValue instanceof SqlNumber) {
            return new BigIntegerValue(this.value.multiply(new BigInteger(sqlValue.toString())));
        } else {
            return new LiteralDecimal(new BigDecimal(this.toString())).multiply(sqlValue);
        }
    }

    public SqlNumber divide(SqlValue sqlValue) throws SQLException {
        if (sqlValue instanceof SqlNumber) {
            return new BigIntegerValue(this.value.divide(new BigInteger(sqlValue.toString())));
        } else {
            return new LiteralDecimal(new BigDecimal(this.toString())).divide(sqlValue);
        }
    }

    public SqlNumber mod(SqlValue sqlValue) throws SQLException {
        if (sqlValue instanceof SqlNumber) {
            return new BigIntegerValue(this.value.mod(new BigInteger(sqlValue.toString())));
        } else {
            return new LiteralDecimal(new BigDecimal(this.toString())).mod(sqlValue);
        }
    }
}
