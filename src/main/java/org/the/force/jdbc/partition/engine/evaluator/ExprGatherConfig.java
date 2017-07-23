package org.the.force.jdbc.partition.engine.evaluator;

/**
 * Created by xuji on 2017/7/24.
 * 对子查询表达式进行筛选的配置
 */
public class ExprGatherConfig {

    private final boolean childClassMatch;

    public ExprGatherConfig(boolean childClassMatch) {
        this.childClassMatch = childClassMatch;
    }

    public boolean isChildClassMatch() {
        return childClassMatch;
    }
}
