package org.the.force.jdbc.partition.engine.executor.dql.select;

import org.the.force.jdbc.partition.engine.router.TableRouter;
import org.the.force.jdbc.partition.engine.executor.dql.tablesource.ParallelJoinedTableSource;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;

/**
 * Created by xuji on 2017/7/13.
 */
public class TableJoinAggregationQuery extends TableJoinRowQuery {

    public TableJoinAggregationQuery(LogicDbConfig logicDbConfig, SQLSelectQueryBlock inputQueryBlock, ParallelJoinedTableSource parallelJoinedTableSource, TableRouter tableRouter) {
        super(logicDbConfig, inputQueryBlock, parallelJoinedTableSource, tableRouter);
    }
}
