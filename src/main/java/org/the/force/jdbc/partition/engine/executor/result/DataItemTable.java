package org.the.force.jdbc.partition.engine.executor.result;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xuji on 2017/6/6.
 */
public class DataItemTable implements RowCursor {

    private final Map<String, Integer> resultSetMetaMap;

    private final RowCursor rowCursor;

    private final LinkedList<DataItemRow> rows = new LinkedList<>();

    private volatile boolean end = false;

    public DataItemTable(RowCursor rowCursor) {
        this.resultSetMetaMap = rowCursor.getResultSetMetaMap();
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

    public Map<String, Integer> getResultSetMetaMap() {
        return resultSetMetaMap;
    }

    public int[] getSqlTypes() {
        return rowCursor.getSqlTypes();
    }

    public void close() throws SQLException {
        rows.clear();
        rowCursor.close();
    }
}
