package org.the.force.jdbc.partition.driver.statement;

import org.the.force.jdbc.partition.driver.JdbcPartitionConnection;
import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.engine.stmt.impl.ParametricStmt;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/29.
 */
public class PPreparedStatement extends AbstractPreparedStatement {


    public PPreparedStatement(JdbcPartitionConnection jdbcPartitionConnection, ParametricStmt logicSql) {
        this(jdbcPartitionConnection, new LogicStmtConfig(), logicSql);
    }

    public PPreparedStatement(JdbcPartitionConnection jdbcPartitionConnection, LogicStmtConfig logicStmtConfig, ParametricStmt logicSql) {
        super(jdbcPartitionConnection, logicStmtConfig, logicSql);
    }

    public boolean execute() throws SQLException {
        return super.execute(parametricSql, logicStmtConfig);
    }

    public ResultSet executeQuery() throws SQLException {
        return super.executeQuery(parametricSql);
    }

    public int executeUpdate() throws SQLException {
        return executeUpdate(parametricSql, logicStmtConfig);
    }

    public void addBatch() throws SQLException {
        parametricSql.addBatch();
    }

    /**
     * 执行的顺序可能与传进来的顺序不同，因此返回值无意义
     */
    public int[] executeBatch() throws SQLException {
        return executeBatch(parametricSql, logicStmtConfig);
    }

    public void clearBatch() throws SQLException {
        parametricSql.clearBatch();
    }





}
