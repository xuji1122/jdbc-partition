package org.the.force.jdbc.partition.engine.executor.query;

import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLTableSourceImpl;

/**
 * Created by xuji on 2017/7/6.
 */
public abstract class ExecutableTableSource extends SQLTableSourceImpl {

    protected final LogicDbConfig logicDbConfig;

    public ExecutableTableSource(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
    }

}
