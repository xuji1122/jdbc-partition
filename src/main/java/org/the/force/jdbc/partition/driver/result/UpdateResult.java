package org.the.force.jdbc.partition.driver.result;

import org.the.force.jdbc.partition.driver.PResult;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xuji on 2017/7/29.
 */
public class UpdateResult implements PResult {

    protected final AtomicInteger updateCount = new AtomicInteger(0);


    public UpdateResult() {

    }

    public void addUpdateCount(int count) {
        updateCount.addAndGet(count);
    }

    public int getUpdateCount() throws SQLException {
        updateCount.get();
        return 0;
    }

    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    public void close() throws SQLException {

    }

    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    public boolean getMoreResults() throws SQLException {
        return false;
    }
}
