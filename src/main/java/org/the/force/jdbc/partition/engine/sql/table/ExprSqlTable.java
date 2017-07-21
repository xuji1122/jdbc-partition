package org.the.force.jdbc.partition.engine.sql.table;

import org.the.force.jdbc.partition.common.PartitionJdbcConstants;
import org.the.force.jdbc.partition.engine.sql.SqlRefer;
import org.the.force.jdbc.partition.engine.sql.SqlTable;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.table.model.LogicTable;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.SQLHint;
import org.the.force.thirdparty.druid.sql.ast.SQLName;
import org.the.force.thirdparty.druid.sql.ast.SQLObject;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.repository.SchemaObject;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/5/23.
 */
public abstract class ExprSqlTable extends SQLExprTableSource implements SqlTable {

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
        this.setParent(sqlExprTableSource.getParent());
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
                logger.warn("could not get executor meta data,table_name=" + tableName, e);
            }
        }
        if (logicTable != null) {
            return logicTable.getColumns();
        }
        return new ArrayList<>();

    }

    public SQLExprTableSource getSQLTableSource() {
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

    protected void accept0(SQLASTVisitor visitor) {
        sqlExprTableSource.accept(visitor);
    }

    @Override
    public int getHintsSize() {
        return sqlExprTableSource.getHintsSize();
    }

    @Override
    public SQLExpr getExpr() {
        return sqlExprTableSource.getExpr();
    }

    @Override
    public void setExpr(SQLExpr expr) {
        sqlExprTableSource.setExpr(expr);
    }

    @Override
    public List<SQLHint> getHints() {
        return sqlExprTableSource.getHints();
    }

    @Override
    public void setHints(List<SQLHint> hints) {
        sqlExprTableSource.setHints(hints);
    }

    @Override
    public List<SQLName> getPartitions() {
        return sqlExprTableSource.getPartitions();
    }

    @Override
    public int getPartitionSize() {
        return sqlExprTableSource.getPartitionSize();
    }

    @Override
    public SQLExpr getFlashback() {
        return sqlExprTableSource.getFlashback();
    }

    @Override
    public void setFlashback(SQLExpr flashback) {
        sqlExprTableSource.setFlashback(flashback);
    }

    @Override
    public SQLObject getParent() {
        return sqlExprTableSource.getParent();
    }

    @Override
    public void addPartition(SQLName partition) {
        sqlExprTableSource.addPartition(partition);
    }

    @Override
    public void setParent(SQLObject parent) {
        sqlExprTableSource.setParent(parent);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return sqlExprTableSource.getAttributes();
    }



    @Override
    public Object getAttribute(String name) {
        return sqlExprTableSource.getAttribute(name);
    }

    @Override
    public void putAttribute(String name, Object value) {
        sqlExprTableSource.putAttribute(name, value);
    }

    @Override
    public void output(StringBuffer buf) {
        sqlExprTableSource.output(buf);
    }

    @Override
    public boolean equals(Object o) {
        return sqlExprTableSource.equals(o);
    }

    @Override
    public Map<String, Object> getAttributesDirect() {
        return sqlExprTableSource.getAttributesDirect();
    }

    @Override
    public void addBeforeComment(String comment) {
        sqlExprTableSource.addBeforeComment(comment);
    }

    @Override
    public int hashCode() {
        return sqlExprTableSource.hashCode();
    }

    @Override
    public String computeAlias() {
        return sqlExprTableSource.computeAlias();
    }

    @Override
    public void addBeforeComment(List<String> comments) {
        sqlExprTableSource.addBeforeComment(comments);
    }

    @Override
    public SQLExprTableSource clone() {
        return sqlExprTableSource.clone();
    }

    @Override
    public void cloneTo(SQLExprTableSource x) {
        sqlExprTableSource.cloneTo(x);
    }

    @Override
    public List<String> getBeforeCommentsDirect() {
        return sqlExprTableSource.getBeforeCommentsDirect();
    }

    @Override
    public SchemaObject getSchemaObject() {
        return sqlExprTableSource.getSchemaObject();
    }

    @Override
    public void setSchemaObject(SchemaObject schemaObject) {
        sqlExprTableSource.setSchemaObject(schemaObject);
    }

    @Override
    public void addAfterComment(String comment) {
        sqlExprTableSource.addAfterComment(comment);
    }

    @Override
    public void addAfterComment(List<String> comments) {
        sqlExprTableSource.addAfterComment(comments);
    }

    @Override
    public List<String> getAfterCommentsDirect() {
        return sqlExprTableSource.getAfterCommentsDirect();
    }

    @Override
    public boolean hasBeforeComment() {
        return sqlExprTableSource.hasBeforeComment();
    }

    @Override
    public boolean hasAfterComment() {
        return sqlExprTableSource.hasAfterComment();
    }
}
