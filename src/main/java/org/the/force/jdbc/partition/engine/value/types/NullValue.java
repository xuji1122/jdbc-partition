package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlNull;
import org.the.force.jdbc.partition.engine.value.SqlNumber;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlValue;
import org.the.force.jdbc.partition.engine.value.TypedValue;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by xuji on 2017/5/17.
 */
public class NullValue extends AbstractSqlValue implements SqlParameter, TypedValue, SqlNull {
    private final int sqlType;
    private final String typeName;


    public NullValue(int sqlType) {
        this.sqlType = sqlType;
        this.typeName = null;
    }

    public NullValue(int sqlType, String typeName) {
        this.sqlType = sqlType;
        this.typeName = typeName;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        if (typeName == null) {
            preparedStatement.setNull(parameterIndex, sqlType);
        } else {
            preparedStatement.setNull(parameterIndex, sqlType, typeName);
        }
    }

    public Object getValue() {
        return null;
    }

    public int getSqlType() {
        return Types.NULL;
    }

    public NullValue clone(SqlValue sqlValue) {
        if (sqlValue instanceof SqlNull) {
            return this;
        } else {
            throw new RuntimeException("!sqlValue instanceof SqlNull");
        }

    }
}
