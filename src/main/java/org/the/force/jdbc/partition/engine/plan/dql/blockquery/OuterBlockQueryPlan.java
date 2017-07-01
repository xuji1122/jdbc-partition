package org.the.force.jdbc.partition.engine.plan.dql.blockquery;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.statement.SQLSelectQueryBlock;
import org.the.force.jdbc.partition.engine.plan.dql.BlockQueryPlan;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

/**
 * Created by xuji on 2017/6/30.
 * 对SQLSelectQueryBlock的结果集在数据层面通过where条件过滤
 */
public class OuterBlockQueryPlan extends BlockQueryPlan {

    private final SQLSelectQueryBlock selectQuery;

    private final SQLExpr where;

    public OuterBlockQueryPlan(LogicDbConfig logicDbConfig, SQLSelectQueryBlock selectQuery, SQLExpr where) throws Exception {
        super(logicDbConfig);
        this.selectQuery = selectQuery;
        this.where = where;
    }

    public SQLSelectQueryBlock getSelectQuery() {
        return selectQuery;
    }

    public SQLExpr getWhere() {
        return where;
    }
}
