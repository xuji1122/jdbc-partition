package org.the.force.jdbc.partition.exception;

import java.sql.SQLFeatureNotSupportedException;

/**
 * Created by xuji on 2017/5/28.
 */
public class UnsupportedSqlOperatorException extends SQLFeatureNotSupportedException {

    public UnsupportedSqlOperatorException(String reason) {
        super(reason);
    }

    private String reason;

    public UnsupportedSqlOperatorException() {
        StackTraceElement[] stackTraceElements = this.getStackTrace();
        reason = stackTraceElements[0].getClassName() + "." + stackTraceElements[0].getMethodName();
    }

    public String getSQLState() {
        return reason;
    }

    public String getMessage() {
        return reason;
    }
}
