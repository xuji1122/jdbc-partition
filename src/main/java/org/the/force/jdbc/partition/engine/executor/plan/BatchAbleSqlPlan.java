package org.the.force.jdbc.partition.engine.executor.plan;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;

/**
 * Created by xuji on 2017/5/18.
 * 可以累积SQL行批量执行的sqlPlan ,主要就是insert,update delete等dml操作
 * 批量执行的模式（单笔执行仅仅是其中的一个case）
 */
public interface BatchAbleSqlPlan extends SqlExecutionPlan {

    /**
     * 累计sql行
     * sql存储的维度
     *    先按照物理库分组存储（存入到PhysicDbExecutor）
     *    再按照物理表分组存储（存入到 PhysicTableExecutor）
     *    同一个物理表的按照参数行累加 存入到PhysicSqlExecutor
     * @param physicDbExecutor
     * @param logicSqlParameterHolder
     * @throws Exception
     */
    void addSqlLine(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception;

}
