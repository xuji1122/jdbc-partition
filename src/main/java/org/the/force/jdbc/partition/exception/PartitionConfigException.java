package org.the.force.jdbc.partition.exception;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/5/16.
 */
public class PartitionConfigException extends SQLException{

    public PartitionConfigException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public PartitionConfigException(String reason) {
        super(reason);
    }
}
