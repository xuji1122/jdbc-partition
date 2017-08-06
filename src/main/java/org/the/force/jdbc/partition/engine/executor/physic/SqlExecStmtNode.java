package org.the.force.jdbc.partition.engine.executor.physic;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xuji on 2017/5/28.
 */
public class SqlExecStmtNode implements SqlExecPhysicNode {

    private final String physicDbName;

    private final LinkedList<LinedSql> linedSqls = new LinkedList<>();

    public SqlExecStmtNode(String physicDbName) {
        this.physicDbName = physicDbName;
    }


    public SqlExecStmtNode(String physicDbName, LinedSql linedSql) {
        this.physicDbName = physicDbName;
        addSql(linedSql);
    }

    public boolean isUpdate() {
        return true;
    }

    public void addSql(LinedSql linedSql) {
        linedSqls.addLast(linedSql);
    }


    public void action(SqlExecCommand template) throws SQLException {
        Connection connection = template.getSqlExecResource().getConnectionAdapter().getConnection(physicDbName);
        Statement statement = connection.createStatement();
        try {

            if (linedSqls.size() < 2) {
                LinedSql linedSql = linedSqls.get(0);
                String sql = linedSql.getSql();
                template.execute(statement, sql, linedSql.getLineNum());
            } else {
                List<Integer> lineNumMap = new ArrayList<>();
                for (LinedSql linedSql : linedSqls) {
                    statement.addBatch(linedSql.getSql());
                    lineNumMap.add(linedSql.getLineNum());
                }
                template.executeBatch(statement, lineNumMap);
            }
        } finally {
            if (linedSqls.size() >= 2) {
                statement.clearBatch();
            }
            clearBatch();
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
        //sb.append("\n");
        for (LinedSql linedSql : linedSqls) {
            for (int i = 0; i < preTabNumber; i++) {
                sb.append("\t");
            }
            linedSql.print(preTabNumber + 1, sb);
        }
    }

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

    public SqlExecPhysicNode get(String sqlKey) {
        throw new UnsupportedOperationException();
    }

    public void put(String sqlKey, SqlExecPhysicNode sqlExecPhysicNode) {
        throw new UnsupportedOperationException();
    }


}
