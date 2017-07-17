package org.the.force.jdbc.partition.engine.executor.dql.select;

import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;

/**
 * Created by xuji on 2017/7/12.
 */
public class PartitionAggregationQuery extends PartitionRowQuery {

    public PartitionAggregationQuery(LogicDbConfig logicDbConfig, SQLSelectQueryBlock inputQueryBlock) {
        super(logicDbConfig, inputQueryBlock);
    }
}
