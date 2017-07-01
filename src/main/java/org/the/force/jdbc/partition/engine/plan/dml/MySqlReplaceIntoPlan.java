package org.the.force.jdbc.partition.engine.plan.dml;

import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.plan.PhysicSqlPlan;
import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.table.impl.LogicTableManagerImpl;

/**
 * Created by xuji on 2017/5/18.
 */
public class MySqlReplaceIntoPlan implements PhysicSqlPlan {

    protected final LogicTableManagerImpl logicTableManager;

    public MySqlReplaceIntoPlan(LogicTableManagerImpl logicTableManager) {
        this.logicTableManager = logicTableManager;
    }


    public void setParameters(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {

    }

}
