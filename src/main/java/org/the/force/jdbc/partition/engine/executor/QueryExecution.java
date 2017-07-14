package org.the.force.jdbc.partition.engine.executor;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.sql.SqlExecutionPlan;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/12.
 */
public interface QueryExecution extends SqlExecutionPlan{


    ResultSet execute(QueryCommand queryCommand,LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException;


}
