package org.the.force.jdbc.partition.engine.executor.dql.select;

import org.the.force.jdbc.partition.engine.parameter.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.engine.router.TableRouter;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.ParallelJoinedTableSource;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/12.
 */
public class TableJoinRowQuery implements QueryExecutor {

    private final LogicDbConfig logicDbConfig;

    private final SQLSelectQueryBlock inputQueryBlock;

    private final ParallelJoinedTableSource parallelJoinedTableSource;

    private final TableRouter tableRouter;

    public TableJoinRowQuery(LogicDbConfig logicDbConfig, SQLSelectQueryBlock inputQueryBlock, ParallelJoinedTableSource parallelJoinedTableSource, TableRouter tableRouter) {
        this.logicDbConfig = logicDbConfig;
        this.inputQueryBlock = inputQueryBlock;
        this.parallelJoinedTableSource = parallelJoinedTableSource;
        this.tableRouter = tableRouter;
    }

    public ResultSet execute(QueryCommand queryCommand, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
        return null;
    }
}
