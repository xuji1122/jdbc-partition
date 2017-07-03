package org.the.force.jdbc.partition.engine.executor.query.item;

import org.the.force.jdbc.partition.resource.table.model.LogicColumn;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/8.
 */
public class LocalDirectItem implements Item {

    private final LogicColumn logicColumn;

    public LocalDirectItem(LogicColumn logicColumn) {
        this.logicColumn = logicColumn;
    }

    public LogicColumn getLogicColumn() {
        return logicColumn;
    }

    public String getColumnName() throws SQLException {
        return logicColumn.getColumnName();
    }

    public String getSchemaName() throws SQLException {
        return logicColumn.logicTable().getSchema();
    }

    public String getTableName() throws SQLException {
        return logicColumn.logicTable().getTableName();
    }

    public String getCatalogName() throws SQLException {
        return logicColumn.logicTable().getCatalog();
    }

    @Override
    public String getColumnLabel() throws SQLException {
        return getColumnName();
    }
}
