package org.the.force.jdbc.partition.engine.sql.parameter;

import org.the.force.jdbc.partition.engine.sql.SqlParameter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;

/**
 * Created by xuji on 2017/5/17.
 */
public class DateSqlParameter implements SqlParameter {
    private Date date;
    private Calendar cal;

    public DateSqlParameter(Date date) {
        this.date = new Date((date.getTime()));
    }

    public DateSqlParameter(Date date, Calendar cal) {
        this.date = new Date(date.getTime());
        this.cal = cal;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        if (cal == null) {
            preparedStatement.setDate(parameterIndex, date);
        } else {
            preparedStatement.setDate(parameterIndex, date, cal);
        }
    }

    public Object getValue() {
        return new Date(date.getTime());
    }

    public int getSqlType() {
        return Types.DATE;
    }
}
