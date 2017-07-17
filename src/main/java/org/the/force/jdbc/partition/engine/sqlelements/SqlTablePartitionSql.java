package org.the.force.jdbc.partition.engine.sqlelements;

import org.the.force.jdbc.partition.engine.parameter.SqlParameter;

import java.util.List;

/**
 * Created by xuji on 2017/7/11.
 */
public class SqlTablePartitionSql {


    private final String sql;

    private final List<SqlParameter> sqlParameters;


    public SqlTablePartitionSql(String sql, List<SqlParameter> sqlParameters) {
        this.sql = sql;
        this.sqlParameters = sqlParameters;
    }

    public String getSql() {
        return sql;
    }

    public List<SqlParameter> getSqlParameters() {
        return sqlParameters;
    }
}
