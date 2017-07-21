package org.the.force.jdbc.partition.engine.executor.dml;

import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.executor.BatchAbleSqlExecutor;
import org.the.force.jdbc.partition.engine.value.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.table.impl.LogicTableManagerImpl;

/**
 * Created by xuji on 2017/5/18.
 */
public class MySqlReplaceIntoExecutor implements BatchAbleSqlExecutor {

    protected final LogicTableManagerImpl logicTableManager;

    public MySqlReplaceIntoExecutor(LogicTableManagerImpl logicTableManager) {
        this.logicTableManager = logicTableManager;
    }


    public void addSqlLine(PhysicDbExecutor physicDbExecutor, LogicSqlParameterHolder logicSqlParameterHolder) throws Exception {

    }

}
