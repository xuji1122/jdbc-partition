package org.the.force.jdbc.partition.driver.result;

import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.engine.stmt.impl.ParamLineStmt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xuji on 2017/7/29.
 * {@link ParamLineStmt}
 */
public class InsertResult implements PResult {

    protected final AtomicInteger updateCount = new AtomicInteger(0);
    /**
     * 返回generated keys
     */
    protected ResultSet generatedKeysResults;

    protected volatile long lastInsertId = -1;

    public int getUpdateCount() throws SQLException {
        return updateCount.get();
    }

    public void addUpdateCount(int count) {
        updateCount.addAndGet(count);
    }

    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return generatedKeysResults;
    }

    public void close() throws SQLException {
        if (generatedKeysResults != null) {
            generatedKeysResults.close();
        }
    }

    public boolean getMoreResults() throws SQLException {
        return false;
    }

    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    public long getLastInsertId() {
        return lastInsertId;
    }

    public void setLastInsertId(long lastInsertId) {
        this.lastInsertId = lastInsertId;
    }
}
