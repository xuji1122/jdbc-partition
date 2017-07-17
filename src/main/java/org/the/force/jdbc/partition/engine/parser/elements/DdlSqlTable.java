package org.the.force.jdbc.partition.engine.parser.elements;

import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLExprTableSource;

/**
 * Created by xuji on 2017/7/17.
 */
public class DdlSqlTable extends ExprSqlTable implements SqlTable {

    public DdlSqlTable(LogicDbConfig logicDbConfig, SQLExprTableSource sqlExprTableSource) {
        super(logicDbConfig, sqlExprTableSource);
    }
}
