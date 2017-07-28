package org.the.force.jdbc.partition.engine.value.types;

import org.the.force.jdbc.partition.common.BeanUtils;
import org.the.force.jdbc.partition.engine.value.AbstractSqlValue;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.engine.value.SqlParameterFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

/**
 * Created by xuji on 2017/5/17.
 */
public class TimestampValue extends AbstractSqlValue implements SqlParameter,SqlParameterFactory {
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


    public SqlParameter parse(String input) {
        List<Integer> parts = BeanUtils.parseDateElements(input);
        LocalDateTime localDate = LocalDateTime.of(parts.get(0), parts.get(1), parts.get(2),parts.get(3), parts.get(4), parts.get(5));
        return new TimestampValue(Timestamp.valueOf(localDate));
    }
}
