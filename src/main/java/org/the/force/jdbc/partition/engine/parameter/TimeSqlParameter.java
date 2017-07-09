package org.the.force.jdbc.partition.engine.parameter;

import java.sql.Time;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;

/**
 * Created by xuji on 2017/5/17.
 */
public class TimeSqlParameter implements SqlParameter {
    private Time time;
    private Calendar cal;

    public TimeSqlParameter(Time time) {
        this.time = time;
    }

    public TimeSqlParameter(Time time, Calendar cal) {
        this.time = time;
        this.cal = cal;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        if (cal == null) {
            preparedStatement.setTime(parameterIndex, time);
        } else {
            preparedStatement.setTime(parameterIndex, time, cal);
        }
    }

    public Object getValue() {
        return time;
    }

    public int getSqlType() {
        return Types.TIME;
    }
}
