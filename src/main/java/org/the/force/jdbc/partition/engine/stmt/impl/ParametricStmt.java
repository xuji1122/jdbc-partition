package org.the.force.jdbc.partition.engine.stmt.impl;

import org.the.force.jdbc.partition.engine.value.SqlParameter;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/30.
 */
public interface ParametricStmt extends SqlLine {

    int getParamSize();

    void setParameter(int parameterIndex, SqlParameter parameter);

    void clearParameters() throws SQLException;

    SqlParameter getSqlParameter(int index);

    void clearBatch() throws SQLException;

    void addBatch() throws SQLException;


}
