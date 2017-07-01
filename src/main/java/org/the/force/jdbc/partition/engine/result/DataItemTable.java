package org.the.force.jdbc.partition.engine.result;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by xuji on 2017/6/6.
 */
public class DataItemTable implements RowCursor {

    private final ResultSetMetaData resultSetMetaData;
    private final RowCursor rowCursor;
    private final LinkedList<DataItemRow> rows = new LinkedList<>();
    private volatile boolean end = false;

    public DataItemTable(RowCursor rowCursor) {
        this.resultSetMetaData = rowCursor.getResultSetMetaData();
        this.rowCursor = rowCursor;

    }

    public DataItemTable loadData(int fetchSize) throws SQLException {
        DataItemRow dataItemRow = null;
        int count = 0;
        while (count < fetchSize && (dataItemRow = rowCursor.next()) != null) {
            rows.addLast(dataItemRow);
            count++;
        }
        if (dataItemRow == null) {
            end = true;
            rowCursor.close();
        }
        return this;
    }

    public DataItemTable loadData() throws SQLException {
        DataItemRow dataItemRow;
        while ((dataItemRow = rowCursor.next()) != null) {
            rows.addLast(dataItemRow);
        }
        return this;
    }

    public DataItemTable orderBy(List<OrderByItem> orderByItems) {
        Collections.sort(rows, new DataItemRowComparator(orderByItems));
        return this;
    }



    public DataItemRow next() throws SQLException {
        if (!rows.isEmpty()) {
            return rows.removeFirst();
        }
        return null;
    }

    public ResultSetMetaData getResultSetMetaData() {
        return resultSetMetaData;
    }


    public void close() throws SQLException {
        rows.clear();
        rowCursor.close();
    }
}
