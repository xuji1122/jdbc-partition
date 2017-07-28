package org.the.force.jdbc.partition.engine.router;

import org.the.force.jdbc.partition.engine.sql.SqlTablePartition;
import org.the.force.jdbc.partition.rule.Partition;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by xuji on 2017/7/11.
 */
public interface TableRouter {

    Map<Partition, SqlTablePartition> route(RouteEvent routeEvent) throws SQLException;

    
}
