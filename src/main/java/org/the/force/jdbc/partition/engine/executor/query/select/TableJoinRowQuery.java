package org.the.force.jdbc.partition.engine.executor.query.select;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.QueryExecution;
import org.the.force.jdbc.partition.engine.parser.router.TableRouter;
import org.the.force.jdbc.partition.engine.executor.query.tablesource.JoinedTableSource;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/12.
 */
public class TableJoinRowQuery implements QueryExecution {

    private final LogicDbConfig logicDbConfig;

    private final SQLSelectQueryBlock inputQueryBlock;

    private final JoinedTableSource joinedTableSource;

    private final TableRouter tableRouter;

    public TableJoinRowQuery(LogicDbConfig logicDbConfig, SQLSelectQueryBlock inputQueryBlock, JoinedTableSource joinedTableSource, TableRouter tableRouter) {
        this.logicDbConfig = logicDbConfig;
        this.inputQueryBlock = inputQueryBlock;
        this.joinedTableSource = joinedTableSource;
        this.tableRouter = tableRouter;
    }

    public ResultSet execute(QueryCommand queryCommand, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
        return null;
    }
}
