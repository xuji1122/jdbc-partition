package org.the.force.jdbc.partition.engine.executor.plan;

/**
 * Created by xuji on 2017/6/4.
 * sql执行计划接口 声明式接口（因为不同的sql执行计划其执行的模式可能根本不同）
 * 要求实现类必须是状态不变的线程安全的实现 方便缓存SQL执行计划，可变的部分通过入参或出参封装
 */
public interface SqlExecutionPlan {

}
