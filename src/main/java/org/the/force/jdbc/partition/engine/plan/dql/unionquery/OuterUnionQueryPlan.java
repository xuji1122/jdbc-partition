package org.the.force.jdbc.partition.engine.plan.dql.unionquery;

import org.druid.sql.ast.SQLExpr;
import org.druid.sql.ast.statement.SQLUnionQuery;
import org.the.force.jdbc.partition.engine.plan.dql.BlockQueryPlan;
import org.the.force.jdbc.partition.engine.plan.dql.UnionQueryPlan;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

/**
 * Created by xuji on 2017/6/30.
 * 对SQLUnionQuery的结果集在数据层面通过where条件过滤
 */
public class OuterUnionQueryPlan extends UnionQueryPlan {

    private final SQLUnionQuery sqlUnionQuery;

    private final SQLExpr where;

    public OuterUnionQueryPlan(LogicDbConfig logicDbConfig, SQLUnionQuery sqlUnionQuery, SQLExpr where) throws Exception {
        super(logicDbConfig);
        this.sqlUnionQuery = sqlUnionQuery;
        this.where = where;
    }


    public SQLUnionQuery getSqlUnionQuery() {
        return sqlUnionQuery;
    }

    public SQLExpr getWhere() {
        return where;
    }
}
