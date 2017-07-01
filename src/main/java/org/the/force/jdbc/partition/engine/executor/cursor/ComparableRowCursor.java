package org.the.force.jdbc.partition.engine.executor.cursor;

import org.the.force.jdbc.partition.engine.result.RowCursor;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/6.
 */
public abstract class ComparableRowCursor implements RowCursor {

    protected final RowCursor left;

    protected final RowCursor right;

    protected final ResultSetMetaData resultSetMetaData;

    public ComparableRowCursor(RowCursor left, RowCursor right, ResultSetMetaData resultSetMetaData) {
        this.left = left;
        this.right = right;
        this.resultSetMetaData = resultSetMetaData;
    }

    public ResultSetMetaData getResultSetMetaData() {
        return resultSetMetaData;
    }


    public void close() throws SQLException {
        left.close();
        right.close();
    }
}
