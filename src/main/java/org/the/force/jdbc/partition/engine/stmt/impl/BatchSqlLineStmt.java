package org.the.force.jdbc.partition.engine.stmt.impl;

import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.driver.result.BatchResult;
import org.the.force.jdbc.partition.driver.result.MultiResult;
import org.the.force.jdbc.partition.engine.stmt.LogicStmt;
import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.resource.SqlExecResource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xuji on 2017/7/30.
 */
public class BatchSqlLineStmt implements LogicStmt {

    private List<SqlLine> linedSqls = new ArrayList<>();

    public int getBatchSize() {
        return linedSqls.size();
    }

    public void clearBatch() throws SQLException {
        linedSqls.clear();
    }

    public void addBatch(SqlLine sqlLine) throws SQLException {
        if (sqlLine != null) {
            sqlLine.setLineNumber(linedSqls.size());
            linedSqls.add(sqlLine);
        }
    }

    public PResult execute(SqlExecResource sqlExecResource, LogicStmtConfig logicStmtConfig) throws SQLException {
        try {
            Iterator<SqlLine> sqlLineIterator = linedSqls.iterator();
            BatchResult batchResult = new BatchResult(sqlExecResource.getLogicDbConfig(), linedSqls.size());
            while (sqlLineIterator.hasNext()) {
                SqlLine sqlLine = sqlLineIterator.next();
                PResult pResult = sqlLine.execute(sqlExecResource, logicStmtConfig);
                batchResult.putPResult(sqlLine.getLineNumber(), pResult);
            }
            return batchResult;
        } finally {
            linedSqls.clear();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        print(0, sb);
        return sb.toString();
    }

    public void print(int preTabNumber, StringBuilder sb) {
        for (SqlLine sqlLine : linedSqls) {
            sb.append("\n");
            for (int i = 0; i < preTabNumber; i++) {
                sb.append("\t");
            }
            sb.append(sqlLine.getLineNumber());
            sqlLine.print(preTabNumber + 1, sb);
        }
    }
}
