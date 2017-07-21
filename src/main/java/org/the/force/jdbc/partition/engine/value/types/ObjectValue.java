package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.literal.LiteralDecimal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/20.
 */
public class ObjectValue extends AbstractSqlValue implements SqlParameter {

    private final Object value;

    public ObjectValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setObject(parameterIndex, value);
    }

    public ObjectValue add(SqlValue sqlValue) throws SQLException {
        BigDecimal bigDecimal = new LiteralDecimal(this.toString()).add(sqlValue).getValue();
        return new ObjectValue(convert(bigDecimal, this.value));
    }

    public ObjectValue subtract(SqlValue sqlValue) throws SQLException {
        BigDecimal bigDecimal = new LiteralDecimal(this.toString()).subtract(sqlValue).getValue();
        return new ObjectValue(convert(bigDecimal, this.value));
    }

    public ObjectValue multiply(SqlValue sqlValue) throws SQLException {
        BigDecimal bigDecimal = new LiteralDecimal(this.toString()).multiply(sqlValue).getValue();
        return new ObjectValue(convert(bigDecimal, this.value));
    }

    public ObjectValue divide(SqlValue sqlValue) throws SQLException {
        BigDecimal bigDecimal = new LiteralDecimal(this.toString()).divide(sqlValue).getValue();
        return new ObjectValue(convert(bigDecimal, this.value));
    }

    public ObjectValue mod(SqlValue sqlValue) throws SQLException {
        BigDecimal bigDecimal = new LiteralDecimal(this.toString()).mod(sqlValue).getValue();
        return new ObjectValue(convert(bigDecimal, this.value));
    }

    public static Object convert(BigDecimal value, Object originalValue) {
        //TODO
        return value;
    }

}
