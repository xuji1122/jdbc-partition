package org.the.force.jdbc.partition.engine.executor;

import org.the.force.jdbc.partition.engine.parameter.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.executor.SqlExecutor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/12.
 */
public interface QueryExecutor extends SqlExecutor {


    ResultSet execute(QueryCommand queryCommand,LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException;


}
