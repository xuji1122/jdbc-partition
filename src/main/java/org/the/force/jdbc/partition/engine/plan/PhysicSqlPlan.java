package org.the.force.jdbc.partition.engine.plan;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;

/**
 * Created by xuji on 2017/5/18.
 * 状态不变的
 * 线程安全的
 */
public interface PhysicSqlPlan extends SqlPlan {


    void setParameters(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception;

}
