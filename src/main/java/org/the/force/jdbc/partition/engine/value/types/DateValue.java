package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.common.BeanUtils;
import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlParameterFactory;
import org.the.force.jdbc.partition.engine.value.TypedValue;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by xuji on 2017/5/17.
 */
public class DateValue extends AbstractSqlValue implements SqlParameter, TypedValue, SqlParameterFactory {
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

    public SqlParameter parse(String input) {
        List<Integer> parts = BeanUtils.parseDateElements(input);
        LocalDate localDate = LocalDate.of(parts.get(0), parts.get(1), parts.get(2));
        return new DateValue(Date.valueOf(localDate));
    }
}
