package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;

import java.util.List;

/**
 * Created by xuji on 2017/6/14.
 */
public abstract class QueriedSqlTable implements SqlTable {

    private final String alias;

    private final SQLTableSource sqlTableSource;

    public QueriedSqlTable(SQLTableSource sqlTableSource) {
        if (sqlTableSource instanceof SQLExprTableSource) {
            throw new SqlParseException("sqlTableSource instanceof SQLExprTableSource");
        }
        this.sqlTableSource = sqlTableSource;
        this.alias = sqlTableSource.getAlias();
    }

    public String getAlias() {
        return alias;
    }

    public String getTableName() {
        return null;
    }

    public final boolean equals(Object o) {
        return sqlTableSource.equals(o);

    }

    public final int hashCode() {
        return sqlTableSource.hashCode();
    }

    public final void setAlias(String alias) {

    }

    public abstract List<String> getReferLabels();

    public SQLTableSource getSQLTableSource() {
        return sqlTableSource;
    }

    public String getRelativeKey() {
        return alias;
    }
}
