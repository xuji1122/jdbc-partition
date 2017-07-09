package org.the.force.jdbc.partition.engine.parameter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;

/**
 * Created by xuji on 2017/5/17.
 */
public class TimestampSqlParameter implements SqlParameter {
    private Timestamp timestamp;
    private Calendar cal;

    public TimestampSqlParameter(Timestamp timestamp) {
        this.timestamp = new Timestamp(timestamp.getTime());
    }

    public TimestampSqlParameter(Timestamp timestamp, Calendar cal) {
        this.timestamp = new Timestamp(timestamp.getTime());
        this.cal = cal;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        if (cal == null) {
            preparedStatement.setTimestamp(parameterIndex, timestamp);
        } else {
            preparedStatement.setTimestamp(parameterIndex, timestamp, cal);
        }
    }

    public Object getValue() {
        return new Timestamp(timestamp.getTime());
    }

    public int getSqlType() {
        return Types.TIMESTAMP;
    }
}
