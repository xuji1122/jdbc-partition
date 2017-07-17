package org.the.force.jdbc.partition.engine.router;

import org.the.force.jdbc.partition.engine.sqlelements.SqlTablePartitionSql;
import org.the.force.jdbc.partition.rule.Partition;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by xuji on 2017/7/11.
 */
public interface TableRouter {

    Map<Partition, SqlTablePartitionSql> route(RouteEvent routeEvent) throws SQLException;

    
}
