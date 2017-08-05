package org.the.force.jdbc.partition.engine.executor.ast;

import org.the.force.jdbc.partition.engine.executor.SqlExecutor;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecDbNode;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/5/18.
 * 可以累积SQL行批量执行的SqlExecution ,主要就是insert,dml delete等dml操作
 * 批量执行的模式（单笔执行是其中的一个case）
 */
public interface BatchExecutableAst extends SqlExecutor {

    /**
     * 累积sql行
     * sql存储的维度
     *    1，按照物理库分组存储 SqlExecDbNode
     *    2，如果是参数化的sql,那么维度是  SqlExecParametricNode --> SqlExecParamLineNode
     *       如果是静态的sql，那么就是sql累积 SqlExecStaticSqlNode
     * @param sqlExecDbNode
     * @param sqlLineExecRequest
     * @throws Exception
     */
    void addExecPhysicNode(SqlExecDbNode sqlExecDbNode, SqlLineExecRequest sqlLineExecRequest) throws SQLException;

}
