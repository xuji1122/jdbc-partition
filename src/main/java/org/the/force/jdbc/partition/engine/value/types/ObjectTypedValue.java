package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.TypedValue;
import org.the.force.jdbc.partition.engine.value.literal.LiteralDecimal;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/5/17.
 */
public class ObjectTypedValue extends AbstractSqlValue implements SqlParameter, TypedValue {

    private final Object value;//可能为null 代表 setNull

    private final int sqlType;//

    public ObjectTypedValue(Object value, int sqlType) {
        this.value = value;
        this.sqlType = sqlType;
    }


    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        if (value == null) {
            preparedStatement.setNull(parameterIndex, sqlType);
        } else {
            preparedStatement.setObject(parameterIndex, value, sqlType);
        }
    }

    public Object getValue() {
        return value;
    }

    public int getSqlType() {
        return sqlType;
    }

    public ObjectTypedValue add(SqlValue sqlValue) throws SQLException {
        BigDecimal value = new LiteralDecimal(this.toString()).add(sqlValue).getValue();
        return new ObjectTypedValue(convert(value,this.sqlType),this.sqlType);
    }

    public ObjectTypedValue subtract(SqlValue sqlValue) throws SQLException {
        BigDecimal value = new LiteralDecimal(this.toString()).subtract(sqlValue).getValue();
        return new ObjectTypedValue(convert(value,this.sqlType),this.sqlType);
    }

    public ObjectTypedValue multiply(SqlValue sqlValue) throws SQLException {
        BigDecimal value = new LiteralDecimal(this.toString()).multiply(sqlValue).getValue();
        return new ObjectTypedValue(convert(value,this.sqlType),this.sqlType);
    }

    public ObjectTypedValue divide(SqlValue sqlValue) throws SQLException {
        BigDecimal value = new LiteralDecimal(this.toString()).divide(sqlValue).getValue();
        return new ObjectTypedValue(convert(value,this.sqlType),this.sqlType);
    }

    public ObjectTypedValue mod(SqlValue sqlValue) throws SQLException {
        BigDecimal value = new LiteralDecimal(this.toString()).mod(sqlValue).getValue();
        return new ObjectTypedValue(convert(value,this.sqlType),this.sqlType);
    }

    public static Object convert(BigDecimal value, int targetType) {
        //TODO
        return value;
    }

}
