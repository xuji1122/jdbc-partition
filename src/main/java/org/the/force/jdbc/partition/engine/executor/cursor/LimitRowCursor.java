package org.the.force.jdbc.partition.engine.executor.cursor;

import org.the.force.jdbc.partition.engine.executor.result.RowCursor;
import org.the.force.jdbc.partition.engine.executor.result.DataItemRow;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by xuji on 2017/6/6.
 */
public class LimitRowCursor extends ComparableRowCursor {

    protected final int offset;

    protected final int rowCount;

    protected int offsetCount = 0;

    protected int totalCount = 0;

    protected boolean end = false;

    public LimitRowCursor(RowCursor left, RowCursor right,int[] sqlTypes, Map<String,Integer> resultSetMetaData, int offset, int rowCount) throws SQLException {
        super(left, right, sqlTypes,resultSetMetaData);
        this.offset = offset;
        this.rowCount = rowCount;
        if (rowCount <= 0) {
            //TODO 异常处理？
            end = true;
            super.close();
        }
    }

    public DataItemRow next() throws SQLException {
        if (end || rowCount <= 0) {
            return null;
        }
        while (offsetCount < offset) {
            DataItemRow dataItemRow = getNext();
            if (dataItemRow != null) {
                offsetCount++;
            } else {
                end = true;
            }
        }
        if (end) {
            super.close();
            return null;
        }
        DataItemRow dataItemRow = getNext();
        if (dataItemRow == null) {
            end = true;
        } else {
            totalCount++;
            if (totalCount >= rowCount) {
                end = true;
            }
        }
        if (end) {
            super.close();
        }
        return dataItemRow;
    }

    protected DataItemRow getNext() throws SQLException {
        DataItemRow dataItemRow = left.next();
        if (dataItemRow == null) {
            dataItemRow = right.next();
        }
        return dataItemRow;
    }




}
