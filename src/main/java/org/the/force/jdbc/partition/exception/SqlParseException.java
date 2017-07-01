package org.the.force.jdbc.partition.exception;

/**
 * Created by xuji on 2017/5/27.
 */
public class SqlParseException extends RuntimeException {

    public SqlParseException(String reason) {
        super(reason);
    }

    public SqlParseException(Throwable cause) {
        super(cause);
    }

    public SqlParseException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
