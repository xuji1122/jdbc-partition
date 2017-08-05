package org.the.force.jdbc.partition.engine.executor.physic;

import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.resource.SqlExecResource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by xuji on 2017/8/3.
 */
public class SqlQueryCommand extends AbstractSqlExecCommand {

    private final SqlExecDbNode sqlExecDbNode;

    public SqlQueryCommand(SqlExecResource sqlExecResource, LogicStmtConfig logicStmtConfig, SqlExecDbNode sqlExecDbNode) {
        super(sqlExecResource, logicStmtConfig);
        this.sqlExecDbNode = sqlExecDbNode;
    }

    public void execute() {

    }

    public PResult getPResult() {
        return null;
    }

    public void execute(PreparedStatement preparedStatement, Integer lineNumber) throws SQLException {

    }

    public void execute(Statement statement,  String sql, Integer lineNumber) throws SQLException {

    }

    public void executeBatch(PreparedStatement preparedStatement, List<Integer> lineOrder) throws SQLException {
        throw new UnsupportedOperationException("executeBatch");
    }

    public void executeBatch(Statement statement, List<Integer> lineOrder) throws SQLException {
        throw new UnsupportedOperationException("executeBatch");
    }


}
