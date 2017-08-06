package org.the.force.jdbc.partition.driver.result;

import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.engine.stmt.impl.MultiParamLineStmt;
import org.the.force.jdbc.partition.engine.stmt.impl.ParamLineStmt;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
public class BatchResult implements PResult {

    private final LogicDbConfig logicDbConfig;
    private Map<Integer, PResult> pResultMap = new ConcurrentHashMap<>();
    private ResultSet generalKeys;
    private Integer updateCount;

    public BatchResult(LogicDbConfig logicDbConfig, int totalSqlNumber) {
        this.logicDbConfig = logicDbConfig;
        pResultMap = new ConcurrentHashMap<>(totalSqlNumber);
    }


    public boolean getMoreResults() throws SQLException {
        return false;
    }

    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    public void putPResult(int sqlNumber, PResult pResult) throws SQLException {
        PResult old = pResultMap.put(sqlNumber, pResult);
        if (old != null) {
            throw new RuntimeException("old PResult exits " + sqlNumber);
        }
    }

    public int getUpdateCount() throws SQLException {
        if (updateCount == null) {
            int total = 0;
            for (PResult pResult : pResultMap.values()) {
                int updateCount = pResult.getUpdateCount();
                if (updateCount > -1) {
                    total += updateCount;
                }
            }
            updateCount = total;
        }
        return updateCount;
    }

    public int[] getUpdateCountArray() throws SQLException {
        List<Integer> array = new ArrayList<>();
        for (PResult pResult : pResultMap.values()) {
            int updateCount = pResult.getUpdateCount();
            if (updateCount > -1) {
                array.add(updateCount);
            }
        }
        int size = array.size();
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            result[i] = array.get(i);
        }
        return result;
    }

    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
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
