package org.the.force.jdbc.partition.engine.sql.parameter;

import org.the.force.jdbc.partition.engine.sql.SqlParameter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/5/17.
 */
public class LogicSqlParameterHolder {

    private List<SqlParameter> sqlParameters = new ArrayList<>();

    private int lineNumber = 0;//从0开始计数

    public void setParameter(int parameterIndex, SqlParameter parameter) {
        int expectSize = sqlParameters.size() + 1;
        if (parameterIndex == expectSize) {
            sqlParameters.add(parameter);
        } else if (parameterIndex < expectSize) {
            sqlParameters.set(parameterIndex - 1, parameter);
        } else {
            int limit = parameterIndex - 1;
            for (int i = expectSize - 1; i < limit; i++) {
                sqlParameters.add(null);
            }
            sqlParameters.add(parameter);
        }
    }

    public void resetLineNumber() throws SQLException {
        clearParameters();
        lineNumber = 0;
    }

    public void addLineNumber() throws SQLException {
        clearParameters();
        lineNumber++;
    }

    public void clearParameters() throws SQLException {
        sqlParameters.clear();
    }

    public boolean hasParam() {
        return !sqlParameters.isEmpty();
    }


    public int getLineNumber() {
        return lineNumber;
    }

    public SqlParameter getSqlParameter(int index) {
        return sqlParameters.get(index);
    }


    public List<SqlParameter> getSqlParameters() {
        return sqlParameters;
    }
}
