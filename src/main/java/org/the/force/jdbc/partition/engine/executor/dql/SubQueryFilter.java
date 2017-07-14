package org.the.force.jdbc.partition.engine.executor.dql;

import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;

import java.util.List;

/**
 * Created by xuji on 2017/7/14.
 * 子查询的封装
 */
public class SubQueryFilter {

    private final LogicDbConfig logicDbConfig;

    private final List<SQLExpr> subQueries;

    public SubQueryFilter(LogicDbConfig logicDbConfig, List<SQLExpr> subQueries) {
        this.logicDbConfig = logicDbConfig;
        this.subQueries = subQueries;
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }

    public List<SQLExpr> getSubQueries() {
        return subQueries;
    }
}
