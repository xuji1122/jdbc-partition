package org.the.force.jdbc.partition.engine.value.literal;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlLiteral;
import org.the.force.jdbc.partition.engine.value.SqlText;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.types.StringValue;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/20.
 */
public class TextValue extends AbstractSqlValue implements SqlLiteral, SqlText {

    private final String text;

    public TextValue(String text) {
        this.text = text;
    }

    public String getValue() {
        return text;
    }

    public String toSql() {
        return "'" + text + "'";
    }

    public String getText() {
        return text;
    }


    public TextValue add(SqlValue sqlValue) throws SQLException {
        return new TextValue(new LiteralDecimal(this.toString()).add(sqlValue).getValue().toPlainString());
    }

    public TextValue subtract(SqlValue sqlValue) throws SQLException {
        return new TextValue(new LiteralDecimal(this.toString()).subtract(sqlValue).getValue().toPlainString());
    }

    public TextValue multiply(SqlValue sqlValue) throws SQLException {
        return new TextValue(new LiteralDecimal(this.toString()).multiply(sqlValue).getValue().toPlainString());
    }

    public TextValue divide(SqlValue sqlValue) throws SQLException {
        return new TextValue(new LiteralDecimal(this.toString()).divide(sqlValue).getValue().toPlainString());
    }

    public TextValue mod(SqlValue sqlValue) throws SQLException {
        return new TextValue(new LiteralDecimal(this.toString()).mod(sqlValue).getValue().toPlainString());
    }

}
