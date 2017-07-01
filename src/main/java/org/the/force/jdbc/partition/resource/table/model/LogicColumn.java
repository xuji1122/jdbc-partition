package org.the.force.jdbc.partition.resource.table.model;

/**
 * Created by xuji on 2017/6/7.
 */
public class LogicColumn {

    private LogicTable logicTable;

    private String columnName;

    public LogicColumn(LogicTable logicTable, String columnName) {
        this.logicTable = logicTable;
        this.columnName = columnName;
    }

    public LogicTable getLogicTable() {
        return logicTable;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LogicColumn that = (LogicColumn) o;

        if (!logicTable.equals(that.logicTable))
            return false;
        return columnName.equals(that.columnName);

    }

    public int hashCode() {
        int result = logicTable.hashCode();
        result = 31 * result + columnName.hashCode();
        return result;
    }

    public String toString() {
        return "LogicColumn{" + "logicTable=" + logicTable + ", columnName='" + columnName + '\'' + '}';
    }
}
