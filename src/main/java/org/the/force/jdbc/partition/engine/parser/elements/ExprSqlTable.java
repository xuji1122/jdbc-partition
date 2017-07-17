package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.jdbc.partition.common.PartitionJdbcConstants;
import org.the.force.jdbc.partition.engine.parser.sqlrefer.SqlReferParser;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.model.LogicTable;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by xuji on 2017/5/23.
 */
public abstract class ExprSqlTable implements SqlTable{

    private Log logger = LogFactory.getLog(ExprSqlTable.class);

    private final SQLExprTableSource sqlExprTableSource;

    private final LogicDbConfig logicDbConfig;
    private final String schema;
    private final String tableName;
    private String alias;
    //关联的table的定义
    private LogicTable logicTable;


    public ExprSqlTable(LogicDbConfig logicDbConfig, SQLExprTableSource sqlExprTableSource) {
        this.logicDbConfig = logicDbConfig;
        this.sqlExprTableSource = sqlExprTableSource;
        this.alias = sqlExprTableSource.getAlias();//大小写敏感
        if (!(sqlExprTableSource.getExpr() instanceof SQLName)) {
            throw new SqlParseException("SQLExprTableSource的expr非sqlName类型");
        }
        SqlRefer sqlRefer = new SqlRefer((SQLName) sqlExprTableSource.getExpr());
        if (sqlRefer.getOwnerName() == null) {
            this.schema = PartitionJdbcConstants.EMPTY_NAME;
        } else {
            this.schema = sqlRefer.getOwnerName();
        }
        this.tableName = sqlRefer.getName();
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
        return sqlExprTableSource.equals(o);
    }

    public int hashCode() {
        return sqlExprTableSource.hashCode();
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

    public List<String> getReferLabels() {
        if (logicDbConfig != null && getLogicTable() == null) {
            LogicTable logicTable;
            try {
                logicTable = logicDbConfig.getLogicTableManager(tableName).getLogicTable();
                setLogicTable(logicTable);
            } catch (SQLException e) {
                logger.warn("could not get select meta data,table_name=" + tableName, e);
            }
        }
        if (logicTable != null) {
            return logicTable.getColumns();
        }
        return new ArrayList<>();

    }

    public SQLTableSource getSQLTableSource() {
        return sqlExprTableSource;
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public String getRelativeKey() {
        if (alias != null) {
            return alias;
        }
        return tableName;
    }
}
