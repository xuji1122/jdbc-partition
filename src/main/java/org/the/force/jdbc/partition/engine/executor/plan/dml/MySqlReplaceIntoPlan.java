package org.the.force.jdbc.partition.engine.executor.plan.dml;

import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.plan.BatchAbleSqlPlan;
import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.table.impl.LogicTableManagerImpl;

/**
 * Created by xuji on 2017/5/18.
 */
public class MySqlReplaceIntoPlan implements BatchAbleSqlPlan {

    protected final LogicTableManagerImpl logicTableManager;

    public MySqlReplaceIntoPlan(LogicTableManagerImpl logicTableManager) {
        this.logicTableManager = logicTableManager;
    }


    public void addSqlLine(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {

    }

}
