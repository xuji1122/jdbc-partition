package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlParameterFactory;
import org.the.force.jdbc.partition.engine.value.SqlText;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.literal.LiteralDecimal;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by xuji on 2017/7/6.
 */
public class StringValue extends AbstractSqlValue implements SqlParameter, SqlText, SqlParameterFactory {

    private final String value;

    public StringValue(String value) {
        this.value = value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setString(parameterIndex, value);
    }

    public String getValue() {
        return value;
    }


    public int getSqlType() {
        return Types.VARCHAR;
    }


    public String getText() {
        return getValue();
    }

    public SqlText clone(SqlText sqlText) {
        return new StringValue(sqlText.getText());
    }

    public StringValue add(SqlValue sqlValue) throws SQLException {
        return new StringValue(new LiteralDecimal(this.toString()).add(sqlValue).getValue().toPlainString());
    }

    public StringValue subtract(SqlValue sqlValue) throws SQLException {
        return new StringValue(new LiteralDecimal(this.toString()).subtract(sqlValue).getValue().toPlainString());
    }

    public StringValue multiply(SqlValue sqlValue) throws SQLException {
        return new StringValue(new LiteralDecimal(this.toString()).multiply(sqlValue).getValue().toPlainString());
    }

    public StringValue divide(SqlValue sqlValue) throws SQLException {
        return new StringValue(new LiteralDecimal(this.toString()).divide(sqlValue).getValue().toPlainString());
    }

    public StringValue mod(SqlValue sqlValue) throws SQLException {
        return new StringValue(new LiteralDecimal(this.toString()).mod(sqlValue).getValue().toPlainString());
    }

    public SqlParameter parse(String input) {
        return new StringValue(input);
    }
}
