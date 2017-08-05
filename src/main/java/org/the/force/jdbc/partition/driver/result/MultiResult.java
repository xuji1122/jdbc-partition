package org.the.force.jdbc.partition.driver.result;

import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.engine.stmt.impl.MultiParamLineStmt;
import org.the.force.jdbc.partition.engine.stmt.impl.ParamLineStmt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by xuji on 2017/7/29.
 * 支持的场景
 * 多条sql语句一次执行的结果集
 * 每条sql对应一个PResult
 * 注意，每条sql本身也是一个复合类型的sql
 * {@link MultiParamLineStmt}
 * {@link ParamLineStmt}
 */
public class MultiResult implements PResult {

    private Map<Integer, PResult> pResultMap = new ConcurrentHashMap<>();
    private PResult currentResult;
    private int currentIndex = -1;
    private int currentUpdateCount = -1;

    public MultiResult(int totalSqlNumber) {
        pResultMap = new ConcurrentHashMap<>(totalSqlNumber);
    }


    public boolean getMoreResults() throws SQLException {
        return getMoreResults(Statement.CLOSE_CURRENT_RESULT);
    }

    /**
     * 对多结果的返回值
     *
     * @param current 对当前结果集的处理策略
     * @return <code>true</code> if the next result is a <code>ResultSet</code>
     * object; <code>false</code> if it is an update count or there are no
     * more results
     * @throws SQLException
     * @see Statement#CLOSE_CURRENT_RESULT
     * @see Statement#KEEP_CURRENT_RESULT
     * @see Statement#CLOSE_ALL_RESULTS  close all previous result set
     */
    public boolean getMoreResults(int current) throws SQLException {
        if (currentIndex < 0) {
            throw new RuntimeException("currentIndex < 0");
        }
        if (pResultMap.isEmpty()) {
            return false;
        }
        if (currentResult instanceof MultiResult) {
            MultiResult multiResult = (MultiResult) currentResult;
            boolean result = multiResult.getMoreResults(current);
            if (result) {//有result set
                return true;
            } else {
                if (!multiResult.isEnd()) {//结果类型是更新而非no more results
                    //has result
                    return false;
                }
            }
        }
        if (isEnd()) {
            return false;
        }
        currentResult = pResultMap.remove(++currentIndex);
        currentUpdateCount = currentResult.getUpdateCount();
        return currentUpdateCount == -1;
    }

    public boolean isEnd() {
        return currentIndex >= pResultMap.size();
    }

    public void putPResult(int sqlNumber, PResult pResult) throws SQLException {
        PResult old = pResultMap.put(sqlNumber, pResult);
        if (old != null) {
            throw new RuntimeException("old PResult exits " + sqlNumber);
        }
        if (sqlNumber == 0) {
            currentIndex = 0;
            currentResult = pResult;
            currentUpdateCount = pResult.getUpdateCount();
        }
    }

    public int getUpdateCount() throws SQLException {
        if (currentIndex < 0) {
            throw new RuntimeException("currentIndex < 0");
        }
        return currentUpdateCount;
    }

    public ResultSet getResultSet() throws SQLException {
        if (currentIndex < 0) {
            throw new RuntimeException("currentIndex < 0");
        }
        return currentResult.getResultSet();
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        if (currentIndex < 0) {
            throw new RuntimeException("currentIndex < 0");
        }
        return currentResult.getGeneratedKeys();
    }

    public void close() throws SQLException {
        for (PResult pResult : pResultMap.values()) {
            try {
                pResult.close();
            } catch (Exception e) {

            }
        }
        pResultMap.clear();
    }
}
