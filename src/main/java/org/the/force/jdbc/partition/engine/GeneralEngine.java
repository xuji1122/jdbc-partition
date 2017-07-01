package org.the.force.jdbc.partition.engine;

import org.the.force.jdbc.partition.driver.JdbcPartitionConnection;
import org.the.force.jdbc.partition.resource.statement.AbstractStatement;

/**
 * Created by xuji on 2017/6/19.
 */
public class GeneralEngine extends AbstractStatement {

    private final JdbcPartitionConnection connection;

    public GeneralEngine(JdbcPartitionConnection connection) {
        this.connection = connection;
    }
}
