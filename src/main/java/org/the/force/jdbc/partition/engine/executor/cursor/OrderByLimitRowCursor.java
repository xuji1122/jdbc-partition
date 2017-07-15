package org.the.force.jdbc.partition.engine.executor.cursor;

import org.the.force.jdbc.partition.engine.result.DataItemRowComparator;
import org.the.force.jdbc.partition.engine.result.RowCursor;
import org.the.force.jdbc.partition.engine.result.DataItemRow;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by xuji on 2017/6/6.
 */
public class OrderByLimitRowCursor extends LimitRowCursor {

    protected final DataItemRowComparator dataItemRowComparator;


    public OrderByLimitRowCursor(RowCursor left, RowCursor right, int[] sqlTypes, Map<String, Integer> resultSetMetaData, int offset, int rowCount,
        DataItemRowComparator dataItemRowComparator) throws SQLException {
        super(left, right, sqlTypes, resultSetMetaData, offset, rowCount);
        this.dataItemRowComparator = dataItemRowComparator;
    }

    protected DataItemRow getNext() throws SQLException {
        return dataItemRowComparator.compareAndReturn(left.next(), right.next());
    }

}
