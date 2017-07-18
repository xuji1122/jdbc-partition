package org.the.force.jdbc.partition.engine.executor.dql.blockquery;

import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.sql.parameter.LogicSqlParameterHolder;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/18.
 */
public class JoinedBlockQueryExecutor implements BlockQueryExecutor{

    private final SQLSelectQueryBlock sqlSelectQueryBlock;

    public JoinedBlockQueryExecutor(SQLSelectQueryBlock sqlSelectQueryBlock) {
        this.sqlSelectQueryBlock = sqlSelectQueryBlock;
    }

    public ResultSet execute(QueryCommand queryCommand, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
        return null;
    }
}
