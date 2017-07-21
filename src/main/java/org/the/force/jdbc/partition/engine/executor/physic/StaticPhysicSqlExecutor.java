package org.the.force.jdbc.partition.engine.executor.physic;

import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.WriteCommand;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xuji on 2017/5/28.
 */
public class StaticPhysicSqlExecutor implements PhysicSqlExecutor {

    private final String physicDbName;

    private final LinkedList<LinedSql> linedSqls = new LinkedList<>();

    public StaticPhysicSqlExecutor(String physicDbName) {
        this.physicDbName = physicDbName;
    }

    public boolean isUpdate() {
        return true;
    }

    public void addSql(LinedSql linedSql) {
        linedSqls.addLast(linedSql);
    }


    public void executeUpdate(WriteCommand template) throws SQLException {
        Connection connection = template.getConnection(physicDbName);
        Statement statement = connection.createStatement();
        try {
            List<Integer> lineNumMap = new ArrayList<>();
            String sql = null;
            if (linedSqls.size() < 2) {
                LinedSql linedSql = linedSqls.get(0);
                lineNumMap.add(linedSql.getLineNum());
                sql = linedSql.getSql();
            } else {
                for (LinedSql linedSql : linedSqls) {
                    statement.addBatch(linedSql.getSql());
                    lineNumMap.add(linedSql.getLineNum());
                }
            }
            int[] result = template.invokeWrite(statement, sql, lineNumMap);
            template.collectResult(lineNumMap, result, statement);
        } finally {
            clearBatch();
            statement.clearBatch();
        }
    }

    public String getSqlKey() {
        return null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        print(0, sb);
        return sb.toString();
    }

    public void print(int preTabNumber, StringBuilder sb) {
        for (LinedSql linedSql : linedSqls) {
            linedSql.print(preTabNumber + 1, sb);
        }
    }

    @Override
    public int sqlSize() {
        return linedSqls.size();
    }

    public void clearParameters(int lineNum) {
        LinedSql last = linedSqls.peekLast();
        while (last != null) {
            if (last.getLineNum() == lineNum) {
                linedSqls.removeLast();
                last = linedSqls.peekLast();
            }
        }
    }

    public void clearBatch() {
        linedSqls.clear();
    }

    public void close() {
        clearBatch();
    }

    public ResultSet executeQuery(QueryCommand executeQueryTemplate) throws SQLException {
        Connection connection = executeQueryTemplate.getConnection(physicDbName);
        Statement statement = null;
        try {
            statement = connection.createStatement();
            executeQueryTemplate.configStatement(statement);
            return executeQueryTemplate.executeQuery(statement, linedSqls.peekLast().getSql());
        } finally {
            if (statement != null) {
                statement.clearBatch();
            }
            linedSqls.clear();
        }
    }
}
