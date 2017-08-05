package org.the.force.jdbc.partition.engine.executor.physic;

import org.the.force.jdbc.partition.engine.value.SqlParameter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xuji on 2017/5/20.
 */
public class SqlExecParamLineNode implements SqlExecPhysicNode {

    private final String sql;
    private final String physicDbName;

    private LinkedList<LinedParameters> sqlParametersBatch = new LinkedList<>();

    public SqlExecParamLineNode(String sql, String physicDbName) {
        this.sql = sql;
        this.physicDbName = physicDbName;
    }

    public SqlExecParamLineNode(String sql, String physicDbName, LinedParameters linedParameters) {
        this.sql = sql;
        this.physicDbName = physicDbName;
        addParameters(linedParameters);
    }

    public void addParameters(LinedParameters linedParameters) {
        sqlParametersBatch.addLast(linedParameters);
    }

    public void clearParameters(int lineNum) {
        if (sqlParametersBatch.isEmpty()) {
            return;
        }
        LinedParameters last = sqlParametersBatch.peekLast();
        while (last != null) {
            if (last.getLineNum() == lineNum) {
                sqlParametersBatch.removeLast();
                last.getSqlParameters().clear();
                last = sqlParametersBatch.peekLast();
            } else {
                break;
            }
        }
    }

    public void clearBatch() {
        for (LinedParameters linedParameters : sqlParametersBatch) {
            List<SqlParameter> sqlParameters = linedParameters.getSqlParameters();
            sqlParameters.clear();
        }
        sqlParametersBatch.clear();
    }

    public void close() {
        clearBatch();
    }

    public String getSql() {
        return sql;
    }

    public String getPhysicDbName() {
        return physicDbName;
    }


    public void action(SqlExecCommand sqlExecCommand) throws SQLException {

        Connection connection = sqlExecCommand.getSqlExecResource().getConnectionAdapter().getConnection(physicDbName);
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = connection.prepareStatement(sql);
            sqlExecCommand.configStatement(preparedStatement);
            if (sqlParametersBatch.size() < 2) {
                LinedParameters linedParameters = sqlParametersBatch.get(0);
                List<SqlParameter> sqlParameters = linedParameters.getSqlParameters();
                sqlExecCommand.setParams(linedParameters.getLineNum(), preparedStatement, sqlParameters);
                sqlExecCommand.execute(preparedStatement, linedParameters.getLineNum());
            } else {
                List<Integer> lineNumMap = new ArrayList<>();
                for (LinedParameters linedParameters : sqlParametersBatch) {
                    List<SqlParameter> sqlParameters = linedParameters.getSqlParameters();
                    lineNumMap.add(linedParameters.getLineNum());
                    sqlExecCommand.setParams(linedParameters.getLineNum(), preparedStatement, sqlParameters);
                    preparedStatement.addBatch();
                }
                sqlExecCommand.executeBatch(preparedStatement, lineNumMap);
            }
        } finally {
            if (preparedStatement != null) {
                if (sqlParametersBatch.size() < 2) {
                    preparedStatement.clearParameters();
                } else {
                    preparedStatement.clearBatch();
                }
            }
            sqlParametersBatch.clear();
        }

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        print(0, sb);
        return sb.toString();
    }

    public void print(int preTabNumber, StringBuilder sb) {
        sb.append("\n");
        for (int i = 0; i < preTabNumber; i++) {
            sb.append("\t");
        }
        for (LinedParameters linedParameters : sqlParametersBatch) {
            linedParameters.print(preTabNumber + 1, sb);
        }
    }

    public String getSqlKey() {
        return sql;
    }


    public int sqlSize() {
        return sqlParametersBatch.size();
    }


    public SqlExecPhysicNode get(String sqlKey) {
        throw new UnsupportedOperationException();
    }

    public void put(String sqlKey, SqlExecPhysicNode sqlExecPhysicNode) {
        throw new UnsupportedOperationException();
    }


    public void addParamLine(LinedParameters linedParameters) {
        this.addParameters(linedParameters);
    }
}
