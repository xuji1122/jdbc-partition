package org.the.force.jdbc.partition.driver.result;

import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.engine.stmt.impl.ParamLineStmt;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/29.
 * {@link ParamLineStmt}
 */
public class QueryResult implements PResult {

    private final ResultSet resultSet;

    public QueryResult(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public int getUpdateCount() throws SQLException {
        return -1;
    }

    public ResultSet getResultSet() throws SQLException {
        return resultSet;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    public void close() throws SQLException {
        if (resultSet != null) {
            resultSet.close();
        }
    }

    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    public boolean getMoreResults() throws SQLException {
        return false;
    }
}
