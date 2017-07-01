package org.the.force.jdbc.partition.engine.plan.model;

import java.util.Set;

/**
 * Created by xuji on 2017/6/14.
 */
public class QueriedSqlTable implements SqlTable {

    private final String alias;

    private final Set<String> columns;

    public QueriedSqlTable(String alias, Set<String> columns) {
        this.alias = alias;
        this.columns = columns;
    }

    public String getAlias() {
        return alias;
    }

    public String getTableName() {
        return alias;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        QueriedSqlTable that = (QueriedSqlTable) o;

        return getAlias().equals(that.getAlias());

    }

    public int hashCode() {
        return getAlias().hashCode();
    }

    public void setAlias(String alias) {

    }

    public Set<String> getColumns() {
        return columns;
    }
}
