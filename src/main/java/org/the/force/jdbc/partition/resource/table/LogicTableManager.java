package org.the.force.jdbc.partition.resource.table;

/**
 * Created by xuji on 2017/6/29.
 */
public interface LogicTableManager {

    String getLogicTableName();

    LogicTableConfig[] getLogicTableConfig();
}
