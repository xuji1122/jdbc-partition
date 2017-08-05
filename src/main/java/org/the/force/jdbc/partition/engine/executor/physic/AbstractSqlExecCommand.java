package org.the.force.jdbc.partition.engine.executor.physic;

import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.engine.value.SqlParameter;
import org.the.force.jdbc.partition.resource.SqlExecResource;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Created by xuji on 2017/8/3.
 */
public abstract class AbstractSqlExecCommand implements SqlExecCommand {

    private final SqlExecResource sqlExecResource;

    private final LogicStmtConfig logicStmtConfig;

    public AbstractSqlExecCommand(SqlExecResource sqlExecResource, LogicStmtConfig logicStmtConfig) {
        this.sqlExecResource = sqlExecResource;
        this.logicStmtConfig = logicStmtConfig;
    }

    //TODO
    public void configStatement(Statement statement) {

    }

    public void setParams(Integer lineNumber, PreparedStatement preparedStatement, List<SqlParameter> sqlParameters) throws SQLException {
        for (int i = 0, limit = sqlParameters.size(); i < limit; i++) {
            SqlParameter sqlParameter = sqlParameters.get(i);
            sqlParameter.set(i + 1, preparedStatement);
        }
        sqlParameters.clear();
    }

    public SqlExecResource getSqlExecResource() {
        return sqlExecResource;
    }

    public LogicStmtConfig getLogicStmtConfig() {
        return logicStmtConfig;
    }
}
