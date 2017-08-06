package org.the.force.jdbc.partition.engine.executor.physic;

import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.driver.result.UpdateResult;
import org.the.force.jdbc.partition.engine.executor.result.UpdateMerger;
import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.resource.SqlExecResource;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by xuji on 2017/6/2.
 */
public class SqlUpdateCommand extends AbstractSqlExecCommand {

    private static Log logger = LogFactory.getLog(SqlUpdateCommand.class);

    private final SqlExecDbNode sqlExecDbNode;

    private final UpdateMerger updateMerger;

    public SqlUpdateCommand(SqlExecResource sqlExecResource, LogicStmtConfig logicStmtConfig, SqlExecDbNode sqlExecDbNode, UpdateMerger updateMerger) {
        super(sqlExecResource, logicStmtConfig);
        this.updateMerger = updateMerger;
        this.sqlExecDbNode = sqlExecDbNode;
    }

    public void execute() throws SQLException {
        sqlExecDbNode.action(this);
    }

    public PResult getPResult() {
        return new UpdateResult(getLogicDbConfig(), updateMerger);
    }

    public void collectResult(List<Integer> lineNumMap, int[] result, Statement statement) {
        for (int i = 0; i < result.length; i++) {
            if (result[i] < 0) {
                updateMerger.addFailed(lineNumMap.get(i), result[i]);
            } else {
                updateMerger.addSuccess(lineNumMap.get(i), result[i]);
            }
        }
    }

    //TODO 获取jdbc-partition设置的主键
    public void setParams(Integer lineNumber, PreparedStatement preparedStatement, List<SqlParameter> sqlParameters) throws SQLException {
        for (int i = 0, limit = sqlParameters.size(); i < limit; i++) {
            SqlParameter sqlParameter = sqlParameters.get(i);
            sqlParameter.set(i + 1, preparedStatement);
        }
        sqlParameters.clear();
    }


    public void execute(PreparedStatement preparedStatement, Integer lineNumber) throws SQLException {
        int result = preparedStatement.executeUpdate();
        updateMerger.addSuccess(lineNumber, result);
    }

    public void execute(Statement statement, String sql, Integer lineNumber) throws SQLException {
        int result = statement.executeUpdate(sql);
        updateMerger.addSuccess(lineNumber, result);
    }

    public void executeBatch(PreparedStatement preparedStatement, List<Integer> lineOrder) throws SQLException {
        int[] results = preparedStatement.executeBatch();
        collectResult(lineOrder, results, preparedStatement);
    }

    public void executeBatch(Statement statement, List<Integer> lineOrder) throws SQLException {
        int[] results = statement.executeBatch();
        collectResult(lineOrder, results, statement);
    }
}
