package org.the.force.jdbc.partition.engine.sql.query;

import org.the.force.jdbc.partition.engine.evaluator.row.RsIndexEvaluator;

/**
 * Created by xuji on 2017/7/14.
 * 约定order by的列必须在结果集中
 */
public class OrderByItem {

    private final RsIndexEvaluator rsIndexEvaluator;

    private final boolean asc;

    public OrderByItem(RsIndexEvaluator rsIndexEvaluator, boolean asc) {
        this.rsIndexEvaluator = rsIndexEvaluator;
        this.asc = asc;
    }

    public RsIndexEvaluator getSelectValueNode() {
        return rsIndexEvaluator;
    }

    public boolean isAsc() {
        return asc;
    }
}
