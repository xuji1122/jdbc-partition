package org.the.force.jdbc.partition.engine.sql;

import org.the.force.jdbc.partition.engine.sql.parameter.SqlValue;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/5/17.
 */
public interface SqlParameter extends SqlValue {

    void set(int parameterIndex, PreparedStatement preparedStatement) throws SQLException;
}
