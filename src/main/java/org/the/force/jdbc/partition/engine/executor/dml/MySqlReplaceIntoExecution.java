package org.the.force.jdbc.partition.engine.executor.dml;

import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.BatchAbleSqlExecution;
import org.the.force.jdbc.partition.engine.parameter.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.table.impl.LogicTableManagerImpl;

/**
 * Created by xuji on 2017/5/18.
 */
public class MySqlReplaceIntoExecution implements BatchAbleSqlExecution {

    protected final LogicTableManagerImpl logicTableManager;

    public MySqlReplaceIntoExecution(LogicTableManagerImpl logicTableManager) {
        this.logicTableManager = logicTableManager;
    }


    public void addSqlLine(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {

    }

}
