package org.the.force.jdbc.partition.engine.executor.query.item;

import org.the.force.jdbc.partition.resource.table.model.LogicColumn;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuji on 2017/6/7.
 */
public class FunctionalItem implements Item {

    private final List<LogicColumn> tableColumns = new ArrayList<>();

    private final String columnLabel;

    private final ItemValueFunction valueFunction;

    public FunctionalItem(String columnLabel, ItemValueFunction valueFunction) {
        this.columnLabel = columnLabel;
        this.valueFunction = valueFunction;
    }

    public ItemValueFunction getValueFunction() {
        return valueFunction;
    }

    public String getColumnLabel() throws SQLException {
        return columnLabel;
    }

    public String getColumnName() throws SQLException {
        if (tableColumns.size() == 1) {
            return tableColumns.get(0).getColumnName();
        }
        return null;
    }

    public String getSchemaName() throws SQLException {
        if (tableColumns.size() == 1) {
            return tableColumns.get(0).logicTable().getSchema();
        }
        return null;
    }

    public String getTableName() throws SQLException {
        if (tableColumns.size() == 1) {
            return tableColumns.get(0).logicTable().getTableName();
        }
        return null;
    }

    public String getCatalogName() {
        if (tableColumns.size() == 1) {
            return tableColumns.get(0).logicTable().getCatalog();
        }
        return null;
    }
}
