package org.the.force.jdbc.partition.engine.executor.plan.dql;

import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSourceImpl;
import org.the.force.thirdparty.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by xuji on 2017/7/6.
 */
public abstract class PlanedTableSource extends SQLTableSourceImpl {

    protected final LogicDbConfig logicDbConfig;

    public PlanedTableSource(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

    protected void accept0(SQLASTVisitor visitor) {

    }
}
