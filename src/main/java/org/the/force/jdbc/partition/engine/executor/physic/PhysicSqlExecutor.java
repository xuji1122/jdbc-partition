package org.the.force.jdbc.partition.engine.executor.physic;

import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.WriteCommand;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/5/28.
 */
public interface PhysicSqlExecutor {

    String getSqlKey();

    void print(int preTabNumber, StringBuilder sb);

    void clearParameters(int lineNum);

    void clearBatch();

    void close();

    int sqlSize();

    void executeUpdate(WriteCommand template) throws SQLException;

    ResultSet executeQuery(QueryCommand executeQueryTemplate) throws SQLException;

}
