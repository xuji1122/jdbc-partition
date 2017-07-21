package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.TypedValue;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by xuji on 2017/7/20.
 */
public class BooleanValue extends AbstractSqlValue implements SqlParameter, TypedValue {

    private final boolean value;

    public BooleanValue(boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    public int getSqlType() {
        return Types.BOOLEAN;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        preparedStatement.setBoolean(parameterIndex, value);
    }

    public BooleanValue clone(SqlValue sqlValue) {
        return new BooleanValue(Boolean.parseBoolean(sqlValue.toString()));
    }
}
