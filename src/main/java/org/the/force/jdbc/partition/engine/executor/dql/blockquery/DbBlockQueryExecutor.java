package org.the.force.jdbc.partition.engine.executor.dql.blockquery;

import org.the.force.jdbc.partition.engine.executor.QueryCommand;
import org.the.force.jdbc.partition.engine.sql.parameter.LogicSqlParameterHolder;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/18.
 * 确保sqlTable被正确设置
 * group by 涉及的排序问题  没有group by 但是列有聚合查询也包括在内
 * order by 涉及的问题
 * limit涉及的问题
 */
public class DbBlockQueryExecutor implements BlockQueryExecutor {

    private final SQLSelectQueryBlock sqlSelectQueryBlock;


    public DbBlockQueryExecutor(SQLSelectQueryBlock sqlSelectQueryBlock) {
        this.sqlSelectQueryBlock = sqlSelectQueryBlock;
    }

    public ResultSet execute(QueryCommand queryCommand, LogicSqlParameterHolder logicSqlParameterHolder) throws SQLException {
        return null;
    }
}
