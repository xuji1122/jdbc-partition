package org.the.force.jdbc.partition.engine.stmt.impl;

import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.engine.stmt.LogicStmt;
import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.resource.SqlExecResource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/7/30.
 */
public class MultiSqlLineStmt implements LogicStmt {

    private List<SqlLine> linedSql = new ArrayList<>();

    public int getBatchSize() {
        return linedSql.size();
    }

    public void clearBatch() throws SQLException {
        linedSql.clear();
    }

    public void addBatch(SqlLine sqlLine) throws SQLException {
        if (sqlLine != null) {
            sqlLine.setLineNumber(linedSql.size());
            linedSql.add(sqlLine);
        }
    }

    public PResult execute(SqlExecResource sqlExecResource,LogicStmtConfig logicStmtConfig) throws SQLException {

        return null;
    }
}
