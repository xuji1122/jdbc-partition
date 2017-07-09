package org.the.force.jdbc.partition.engine.parser.elements;

import java.util.Set;

/**
 * Created by xuji on 2017/6/14.
 */
public abstract class QueriedSqlTable implements SqlTable {

    private final String alias;

    public QueriedSqlTable(String alias) {
        this.alias = alias;
    }

    public String getAlias() {
        return alias;
    }

    public String getTableName() {
        return alias;
    }

    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        QueriedSqlTable that = (QueriedSqlTable) o;

        return getAlias().equals(that.getAlias());

    }

    public final int hashCode() {
        return getAlias().hashCode();
    }

    public final void setAlias(String alias) {

    }

    public abstract Set<String> getColumns();

}
