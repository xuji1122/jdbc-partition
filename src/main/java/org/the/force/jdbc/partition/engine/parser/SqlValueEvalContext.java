package org.the.force.jdbc.partition.engine.parser;

import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

/**
 * Created by xuji on 2017/5/18.
 */
public class SqlValueEvalContext {

    private final LogicDbConfig logicDbConfig;

    private final LogicSqlParameterHolder logicSqlParameterHolder;

    private int paramFromIndex;

    public SqlValueEvalContext(LogicDbConfig logicDbConfig, LogicSqlParameterHolder logicSqlParameterHolder) {
        this.logicDbConfig = logicDbConfig;
        this.logicSqlParameterHolder = logicSqlParameterHolder;
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public LogicSqlParameterHolder getLogicSqlParameterHolder() {
        return logicSqlParameterHolder;
    }

    public int getParamFromIndex() {
        return paramFromIndex;
    }

    public void setParamFromIndex(int paramFromIndex) {
        this.paramFromIndex = paramFromIndex;
    }

}
