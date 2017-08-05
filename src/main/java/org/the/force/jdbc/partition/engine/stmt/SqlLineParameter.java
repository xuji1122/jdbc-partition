package org.the.force.jdbc.partition.engine.stmt;

import org.the.force.jdbc.partition.engine.value.SqlParameter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/29.
 */
public class SqlLineParameter {


    private int lineNumber;

    private final int paramSize;

    private final List<SqlParameter> sqlParameters;

    public SqlLineParameter(int lineNumber, int paramSize) {
        this.lineNumber = lineNumber;
        this.paramSize = paramSize;
        sqlParameters = new ArrayList<>(paramSize);
    }

    /**
     * 从1开始计数 这个是给客户端使用的的外部API
     *
     * @param parameterIndex
     * @param parameter
     */
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

    public void clearParameters() throws SQLException {
        sqlParameters.clear();
    }

    /**
     * 这个是内部的api,从0开始计数
     *
     * @param index
     * @return
     */
    public SqlParameter getSqlParameter(int index) {
        return sqlParameters.get(index);
    }

    public List<SqlParameter> getSqlParameters() {

        return sqlParameters;
    }

    public boolean hasParam() {
        return !sqlParameters.isEmpty();
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getParamSize() {
        return paramSize;
    }


}
