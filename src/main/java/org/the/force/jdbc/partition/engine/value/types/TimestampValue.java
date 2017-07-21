package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlParameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

/**
 * Created by xuji on 2017/5/17.
 */
public class TimestampValue extends AbstractSqlValue implements SqlParameter {
    private final Timestamp value;
    private Calendar cal;

    public TimestampValue(Timestamp value) {
        this.value = new Timestamp(value.getTime());
    }

    public TimestampValue(Timestamp value, Calendar cal) {
        this.value = new Timestamp(value.getTime());
        this.cal = cal;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        if (cal == null) {
            preparedStatement.setTimestamp(parameterIndex, value);
        } else {
            preparedStatement.setTimestamp(parameterIndex, value, cal);
        }
    }

    public Object getValue() {
        return new Timestamp(value.getTime());
    }

    public int getSqlType() {
        return Types.TIMESTAMP;
    }


}
