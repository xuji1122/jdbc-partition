package org.the.force.jdbc.partition.resource.table;

import org.the.force.jdbc.partition.resource.table.model.LogicTable;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/29.
 */
public interface LogicTableManager {

    String getLogicTableName();

    LogicTableConfig[] getLogicTableConfig();

    LogicTable getLogicTable() throws SQLException;
}
