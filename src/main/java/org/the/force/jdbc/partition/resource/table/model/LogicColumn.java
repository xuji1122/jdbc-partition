package org.the.force.jdbc.partition.resource.table.model;

import org.the.force.thirdparty.druid.util.JdbcUtils;

/**
 * Created by xuji on 2017/6/7.
 */
public class LogicColumn {

    private final LogicTable logicTable;

    private final String columnName;

    private final int sqlDataType;//jdbcType

    public LogicColumn(LogicTable logicTable, String columnName, int sqlDataType) {
        this.logicTable = logicTable;
        this.columnName = columnName;
        this.sqlDataType = sqlDataType;
    }


    public LogicTable logicTable() {
        return logicTable;
    }

    public String getColumnName() {
        return columnName;
    }

    public int getSqlDataType() {
        return sqlDataType;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LogicColumn that = (LogicColumn) o;

        if (!logicTable().equals(that.logicTable()))
            return false;
        return getColumnName().equals(that.getColumnName());
    }

    @Override
    public int hashCode() {
        int result = logicTable().hashCode();
        result = 31 * result + getColumnName().hashCode();
        return result;
    }

    public String toString() {
        return columnName + ":" + JdbcUtils.getTypeName(sqlDataType);
    }

}
