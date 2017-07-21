package org.the.force.jdbc.partition.engine.executor.physic;

import com.mysql.jdbc.Statement;
import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.WriteCommand;
import org.the.force.jdbc.partition.engine.value.SqlParameter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xuji on 2017/5/20.
 */
public class PreparedPhysicSqlExecutor implements PhysicSqlExecutor {

    private final String sql;
    private final String physicDbName;

    private LinkedList<LinedParameters> sqlParametersBatch = new LinkedList<>();

    public PreparedPhysicSqlExecutor(String sql, String physicDbName) {
        this.sql = sql;
        this.physicDbName = physicDbName;
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


    public void executeUpdate(WriteCommand template) throws SQLException {

        Connection connection = template.getConnection(physicDbName);
        PreparedStatement preparedStatement = null;

        try {
            if (template.returnGeneralKeys()) {
                preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            } else {
                preparedStatement = connection.prepareStatement(sql);
            }
            template.configStatement(preparedStatement);
            List<Integer> lineNumMap = new ArrayList<>();
            if (sqlParametersBatch.size() < 2) {
                LinedParameters linedParameters = sqlParametersBatch.get(0);
                List<SqlParameter> sqlParameters = linedParameters.getSqlParameters();
                template.setParams(preparedStatement, sqlParameters);
                lineNumMap.add(linedParameters.getLineNum());
            } else {
                for (LinedParameters linedParameters : sqlParametersBatch) {
                    List<SqlParameter> sqlParameters = linedParameters.getSqlParameters();
                    lineNumMap.add(linedParameters.getLineNum());
                    template.setParams(preparedStatement, sqlParameters);
                    preparedStatement.addBatch();
                }
            }
            int[] result = template.invokeWrite(preparedStatement, null, lineNumMap);
            template.collectResult(lineNumMap, result, preparedStatement);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.clearParameters();
                preparedStatement.clearBatch();
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
        sb.append(sql);
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

    public ResultSet executeQuery(QueryCommand executeQueryTemplate) throws SQLException {
        Connection connection = executeQueryTemplate.getConnection(physicDbName);
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            executeQueryTemplate.configStatement(preparedStatement);
            LinedParameters linedParameters = sqlParametersBatch.peekLast();
            List<SqlParameter> sqlParameters = linedParameters.getSqlParameters();
            executeQueryTemplate.setParams(preparedStatement, sqlParameters);
            return executeQueryTemplate.executeQuery(preparedStatement, null);
        } finally {
            if (preparedStatement != null) {
                preparedStatement.clearParameters();
                preparedStatement.clearBatch();
            }
            sqlParametersBatch.clear();
        }
    }
}
