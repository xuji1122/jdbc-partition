package org.the.force.jdbc.partition.engine.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by xuji on 2017/5/17.
 */
public class NullSqlParameter implements SqlParameter {
    private int sqlType;
    private String typeName;

    public NullSqlParameter(int sqlType) {
        this.sqlType = sqlType;
    }

    public NullSqlParameter(int sqlType, String typeName) {
        this.sqlType = sqlType;
        this.typeName = typeName;
    }

    @Override
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
}
