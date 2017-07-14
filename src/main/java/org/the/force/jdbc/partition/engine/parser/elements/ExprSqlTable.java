package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.jdbc.partition.common.PartitionJdbcConstants;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.model.LogicTable;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by xuji on 2017/5/23.
 */
public class ExprSqlTable implements SqlTable {

    private Log logger = LogFactory.getLog(ExprSqlTable.class);

    private final LogicDbConfig logicDbConfig;
    private final String schema;
    private final String tableName;
    private String alias;
    //关联的table的定义
    private LogicTable logicTable;

    public ExprSqlTable(LogicDbConfig logicDbConfig, String schema, String tableName, String alias) {
        this.logicDbConfig = logicDbConfig;
        if (schema == null || schema.length() < 1) {
            schema = PartitionJdbcConstants.EMPTY_NAME;
        }
        this.schema = schema;
        if (tableName == null || tableName.length() < 1) {
            tableName = PartitionJdbcConstants.EMPTY_NAME;
        }
        this.tableName = tableName;
        this.alias = alias;
    }

    public String getSchema() {
        return schema;
    }

    public String getTableName() {
        return tableName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ExprSqlTable that = (ExprSqlTable) o;

        if (!getSchema().equalsIgnoreCase(that.getSchema()))
            return false;
        return getTableName().equalsIgnoreCase(that.getTableName());

    }

    public int hashCode() {
        int result = getSchema().toLowerCase().hashCode();
        result = 31 * result + getTableName().toLowerCase().hashCode();
        return result;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(schema).append(".").append(tableName);
        if (alias != null) {
            sb.append("\t").append(alias);
        }
        return sb.toString();
    }

    public LogicTable getLogicTable() {
        return logicTable;
    }

    public void setLogicTable(LogicTable logicTable) {
        this.logicTable = logicTable;
    }

    public Set<String> getReferLabels() {
        if (logicDbConfig != null && getLogicTable() == null) {
            LogicTable logicTable = null;
            try {
                logicTable = logicDbConfig.getLogicTableManager(tableName).getLogicTable();
                setLogicTable(logicTable);
            } catch (SQLException e) {
                logger.warn("could not get select meta data,table_name=" + tableName,e);
            }
        }
        if (logicTable != null) {
            return logicTable.getColumns();
        }
        return new HashSet<>();

    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }


}
