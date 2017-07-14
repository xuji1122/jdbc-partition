package org.the.force.jdbc.partition.engine.executor.dql.select;

import org.the.force.jdbc.partition.engine.parser.elements.SqlColumn;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.ast.SQLExpr;
import org.the.force.thirdparty.druid.sql.ast.expr.SQLInListExpr;
import org.the.force.thirdparty.druid.sql.ast.statement.SQLSelectQueryBlock;

import java.util.Map;

/**
 * Created by xuji on 2017/7/12.
 */
public class PartitionAggregationQuery extends PartitionRowQuery {

    public PartitionAggregationQuery(LogicDbConfig logicDbConfig, SQLSelectQueryBlock inputQueryBlock,  Map<SqlColumn, SQLExpr> currentTableColumnValueMap,
        Map<SqlColumn, SQLInListExpr> currentTableColumnInValuesMap) {
        super(logicDbConfig, inputQueryBlock, currentTableColumnValueMap, currentTableColumnInValuesMap);
    }

}
