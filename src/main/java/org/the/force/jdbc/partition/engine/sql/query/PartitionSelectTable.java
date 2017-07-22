package org.the.force.jdbc.partition.engine.sql.query;

import org.the.force.jdbc.partition.engine.sql.ConditionalSqlTable;

/**
 * Created by xuji on 2017/7/13
 * 对应于{@link org.the.force.jdbc.partition.engine.executor.dql.partition.PartitionBlockQueryExecutor}
 * 的查询结果集的table结构
 */
public class PartitionSelectTable extends SelectTable {

    public PartitionSelectTable(ConditionalSqlTable sqlTable, boolean distinctAll) {
        super(sqlTable, distinctAll);
    }
}
