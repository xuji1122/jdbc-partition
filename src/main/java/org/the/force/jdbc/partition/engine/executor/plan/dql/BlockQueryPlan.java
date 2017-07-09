package org.the.force.jdbc.partition.engine.executor.plan.dql;

import org.the.force.jdbc.partition.engine.executor.plan.QueryPlan;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSourceImpl;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by xuji on 2017/7/1.
 */
public abstract class BlockQueryPlan extends SQLTableSourceImpl implements QueryPlan {

    private final LogicDbConfig logicDbConfig;


    public BlockQueryPlan(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    protected void accept0(SQLASTVisitor visitor) {

    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }
}
