package org.the.force.jdbc.partition.engine.stmt;

import org.the.force.jdbc.partition.driver.PResult;
import org.the.force.jdbc.partition.resource.SqlExecResource;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/5/17.
 */
public interface LogicStmt {

    /**
     * logic sql执行
     * 执行之后，无论成功还是失败都会清空参数或batch
     * @param sqlExecResource
     * @return
     * @throws SQLException
     */
    PResult execute(SqlExecResource sqlExecResource, LogicStmtConfig logicStmtConfig) throws SQLException;

}
