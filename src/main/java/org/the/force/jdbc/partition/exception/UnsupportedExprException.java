package org.the.force.jdbc.partition.exception;

import java.sql.SQLException;

/**
 * Created by xuji on 2017/5/21.
 */
public class UnsupportedExprException extends SQLException {

    public UnsupportedExprException(String message) {
        super(message);
    }
}
