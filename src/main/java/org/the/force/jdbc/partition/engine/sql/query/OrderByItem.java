package org.the.force.jdbc.partition.engine.sql.query;

import org.the.force.jdbc.partition.engine.evaluator.row.RsIndexEvaluator;
import org.the.force.thirdparty.druid.sql.ast.SQLOrderingSpecification;

/**
 * Created by xuji on 2017/7/14.
 * 约定order by的列必须在结果集中
 */
public class OrderByItem {

    private final RsIndexEvaluator rsIndexEvaluator;

    private SQLOrderingSpecification sqlOrderingSpecification;

    public OrderByItem(RsIndexEvaluator rsIndexEvaluator, SQLOrderingSpecification sqlOrderingSpecification) {
        this.rsIndexEvaluator = rsIndexEvaluator;
        this.sqlOrderingSpecification = sqlOrderingSpecification;
    }

    public boolean isAsc() {
        return sqlOrderingSpecification.equals(SQLOrderingSpecification.ASC);
    }

    public RsIndexEvaluator getRsIndexEvaluator() {
        return rsIndexEvaluator;
    }

    public SQLOrderingSpecification getSqlOrderingSpecification() {
        return sqlOrderingSpecification;
    }
}
