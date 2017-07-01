package org.the.force.jdbc.partition.exception;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/5/25.
 */
public class PartitionSystemException extends SQLException{

    public PartitionSystemException(Throwable cause) {
        super(cause);
    }

    public PartitionSystemException(String reason) {
        super(reason);
    }
}
