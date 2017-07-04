package org.the.force.jdbc.partition.engine.plan.dql.blockquery;

import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.jdbc.partition.engine.plan.dql.BlockQueryPlan;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

/**
 * Created by xuji on 2017/6/30.
 * 对SQLSelectQueryBlock的结果集在数据层面通过where条件过滤
 */
public class OuterBlockQueryPlan extends BlockQueryPlan {

    private final SQLSelectQueryBlock selectQuery;

    private final SQLExpr outerCondition;

    public OuterBlockQueryPlan(LogicDbConfig logicDbConfig, SQLSelectQueryBlock selectQuery, SQLExpr outerCondition) throws Exception {
        super(logicDbConfig);
        this.selectQuery = selectQuery;
        this.outerCondition = outerCondition;
    }

    public SQLSelectQueryBlock getSelectQuery() {
        return selectQuery;
    }

    public SQLExpr getOuterCondition() {
        return outerCondition;
    }
}
