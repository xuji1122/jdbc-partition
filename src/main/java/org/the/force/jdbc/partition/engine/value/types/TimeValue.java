package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlParameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.util.Calendar;

/**
 * Created by xuji on 2017/5/17.
 */
public class TimeValue extends AbstractSqlValue implements SqlParameter {
    private final Time value;
    private Calendar cal;

    public TimeValue(Time value) {
        this.value = value;
    }

    public TimeValue(Time value, Calendar cal) {
        this.value = value;
        this.cal = cal;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        if (cal == null) {
            preparedStatement.setTime(parameterIndex, value);
        } else {
            preparedStatement.setTime(parameterIndex, value, cal);
        }
    }

    public Object getValue() {
        return value;
    }

    public int getSqlType() {
        return Types.TIME;
    }

}
