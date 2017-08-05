package org.the.force.jdbc.partition.driver;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/29.
 */
public interface PResult {

    /**
     * 针对当前结果集
     *
     * @return
     * @throws SQLException
     */
    int getUpdateCount() throws SQLException;

    ResultSet getResultSet() throws SQLException;

    ResultSet getGeneratedKeys() throws SQLException;

    void close() throws SQLException;


    boolean getMoreResults() throws SQLException;

    /**
     * 对多结果的返回值（至少含有2个），从第二个结果开始
     *
     * @param current 对当前结果集的处理策略
     * @return
     * @throws SQLException
     * @see java.sql.Statement#CLOSE_CURRENT_RESULT
     * @see java.sql.Statement#KEEP_CURRENT_RESULT
     * @see java.sql.Statement#CLOSE_ALL_RESULTS  close all previous result set
     */
    boolean getMoreResults(int current) throws SQLException;

}
