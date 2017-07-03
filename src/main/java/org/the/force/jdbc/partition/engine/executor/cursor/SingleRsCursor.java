package org.the.force.jdbc.partition.engine.executor.cursor;

import org.the.force.jdbc.partition.engine.result.DataItemRow;
import org.the.force.jdbc.partition.engine.result.RowCursor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/6.
 */
public class SingleRsCursor implements RowCursor {

    private final ResultSet rs;

    private final ResultSetMetaData rsMetaData;

    public SingleRsCursor(ResultSet rs, ResultSetMetaData rsMetaData) {
        this.rs = rs;
        this.rsMetaData = rsMetaData;
    }

    public DataItemRow next() throws SQLException {
        if (rs.next()) {
            return new DataItemRow(rs, rsMetaData);
        } else {
            return null;
        }
    }

    public ResultSetMetaData getResultSetMetaData() {
        return rsMetaData;
    }

    public void close() throws SQLException {
        rs.close();
    }
}
