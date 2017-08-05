package org.the.force.jdbc.partition.resource.executor;

/**
 * Created by xuji on 2017/6/2.
 */
public class SqlKey {

    private final String sql;

    public SqlKey(String sql) {
        this.sql = sql;
    }

    public String getKey() {
        return sql.toLowerCase();
    }

    public String getSql() {
        return sql;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        SqlKey sqlKey = (SqlKey) o;

        return getKey().equals(sqlKey.getKey());

    }

    public int hashCode() {
        return getKey().hashCode();
    }

    public String toString() {
        return sql;
    }

}
