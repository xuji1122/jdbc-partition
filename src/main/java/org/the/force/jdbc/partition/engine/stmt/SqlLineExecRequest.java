package org.the.force.jdbc.partition.engine.stmt;

import org.the.force.jdbc.partition.resource.SqlExecResource;

/**
 * Created by xuji on 2017/8/4.
 */
public class SqlLineExecRequest {

    private final SqlExecResource sqlExecResource;

    private final LogicStmtConfig logicStmtConfig;

    private final SqlLineParameter sqlLineParameter;

    public SqlLineExecRequest(SqlExecResource sqlExecResource, LogicStmtConfig logicStmtConfig, SqlLineParameter sqlLineParameter) {
        this.sqlExecResource = sqlExecResource;
        this.logicStmtConfig = logicStmtConfig;
        this.sqlLineParameter = sqlLineParameter;
    }

    public SqlExecResource getSqlExecResource() {
        return sqlExecResource;
    }

    public SqlLineParameter getSqlLineParameter() {
        return sqlLineParameter;
    }

    public LogicStmtConfig getLogicStmtConfig() {
        return logicStmtConfig;
    }
}
