package org.the.force.jdbc.partition.engine.executor.cursor;

import org.the.force.jdbc.partition.engine.result.RowCursor;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by xuji on 2017/6/6.
 */
public abstract class ComparableRowCursor implements RowCursor {

    protected final RowCursor left;

    protected final RowCursor right;

    private final int[] sqlTypes;

    protected final Map<String, Integer> resultSetMetaData;

    public ComparableRowCursor(RowCursor left, RowCursor right, int[] sqlTypes, Map<String, Integer> resultSetMetaData) {
        this.left = left;
        this.right = right;
        this.sqlTypes = sqlTypes;
        this.resultSetMetaData = resultSetMetaData;
    }

    public Map<String, Integer> getResultSetMetaMap() {
        return resultSetMetaData;
    }

    public int[] getSqlTypes() {
        return sqlTypes;
    }

    public void close() throws SQLException {
        left.close();
        right.close();
    }
}
