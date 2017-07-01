package org.the.force.jdbc.partition.resource.sql;

/**
 * Created by xuji on 2017/6/2.
 */
public class SqlKey {

    private final String sql;

    public SqlKey(String sql) {
        this.sql = sql;
    }

    public String getKey() {
        return toSqlKey(sql);
    }

    public String getSql() {
        return sql;
    }

    private String toSqlKey(String originSql) {

        StringBuilder sb = new StringBuilder(originSql.length() - 2);
        for (int i = 0, size = originSql.length(); i < size; i++) {
            char ch = originSql.charAt(i);
            if (ch > ' ') {
                sb.append(Character.toLowerCase(ch));
            }
        }
        return sb.toString();
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
