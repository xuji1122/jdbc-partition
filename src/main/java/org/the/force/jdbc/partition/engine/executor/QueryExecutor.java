package org.the.force.jdbc.partition.engine.executor;

import org.the.force.jdbc.partition.resource.executor.SqlExecutor;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQuery;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSource;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/12.
 */
public interface QueryExecutor extends SqlExecutor, SQLTableSource, SQLSelectQuery {

    ResultSet execute(SqlExecutionContext executionContext) throws SQLException;

    SQLSelectQuery getStatement();

    void init();
}
