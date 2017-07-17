package org.the.force.jdbc.partition.engine.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/5/17.
 */
public class ObjectSqlParameter implements SqlParameter {

    private final Object parameter;//可能为null 代表 setNull
    private final Integer sqlType;//可以为 null 代表 setObject

    public ObjectSqlParameter(Object parameter, Integer sqlType) {
        this.parameter = parameter;
        this.sqlType = sqlType;
    }

    public ObjectSqlParameter(Object parameter) {
        this.parameter = parameter;
        this.sqlType = null;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        if (parameter == null) {
            preparedStatement.setNull(parameterIndex, sqlType);
        } else if (sqlType == null) {
            preparedStatement.setObject(parameterIndex, parameterIndex);
        } else {
            preparedStatement.setObject(parameterIndex, parameter, sqlType);
        }
    }

    public Object getValue() {
        return parameter;
    }

    public int getSqlType() {
        if (sqlType != null) {
            return sqlType.intValue();
        } else {
            return 0;
        }
    }
}
