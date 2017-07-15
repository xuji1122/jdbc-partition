package org.the.force.jdbc.partition.engine.executor.eval;

import org.the.force.jdbc.partition.engine.parser.elements.SqlRefer;
import org.the.force.jdbc.partition.engine.parser.elements.SqlTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;

import java.util.Map;

/**
 * Created by xuji on 2017/5/18.
 */
public class SqlValueEvalContext {

    private final LogicDbConfig logicDbConfig;

    private final Map<SqlRefer, Integer> sqlReferIntegerMap;//当index 为-1时使用结果集的columnName匹配

    private final Map<String, SqlTable> sqlTableMap;

    public SqlValueEvalContext(LogicDbConfig logicDbConfig) {
        this.logicDbConfig = logicDbConfig;
        sqlReferIntegerMap = null;
        sqlTableMap = null;
    }

    public SqlValueEvalContext(LogicDbConfig logicDbConfig, Map<SqlRefer, Integer> sqlReferIntegerMap, Map<String, SqlTable> sqlTableMap) {
        this.logicDbConfig = logicDbConfig;
        this.sqlReferIntegerMap = sqlReferIntegerMap;
        this.sqlTableMap = sqlTableMap;
    }

    public LogicDbConfig getLogicDbConfig() {
        return logicDbConfig;
    }


    public Map<SqlRefer, Integer> getSqlReferIntegerMap() {
        return sqlReferIntegerMap;
    }

    public Map<String, SqlTable> getSqlTableMap() {
        return sqlTableMap;
    }

}
