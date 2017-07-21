package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.TypedValue;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;

/**
 * Created by xuji on 2017/5/17.
 */
public class DateValue extends AbstractSqlValue implements SqlParameter,TypedValue {
    private final Date value;
    private Calendar cal;

    public DateValue(Date value) {
        this.value = new Date((value.getTime()));
    }

    public DateValue(Date value, Calendar cal) {
        this.value = new Date(value.getTime());
        this.cal = cal;
    }

    public void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException {
        if (cal == null) {
            preparedStatement.setDate(parameterIndex, value);
        } else {
            preparedStatement.setDate(parameterIndex, value, cal);
        }
    }

    public Object getValue() {
        return new Date(value.getTime());
    }

    public int getSqlType() {
        return Types.DATE;
    }

}
