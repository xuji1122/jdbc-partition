package org.the.force.jdbc.partition.engine.executor.cursor;

import org.the.force.jdbc.partition.engine.result.DataItemRowComparator;
import org.the.force.jdbc.partition.engine.result.RowCursor;
import org.the.force.jdbc.partition.engine.result.DataItemRow;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/6/6.
 */
public class OrderByRowCursor extends ComparableRowCursor {

    protected final DataItemRowComparator dataItemRowComparator;

    public OrderByRowCursor(RowCursor left, RowCursor right, ResultSetMetaData resultSetMetaData, DataItemRowComparator dataItemRowComparator) {
        super(left, right, resultSetMetaData);
        this.dataItemRowComparator = dataItemRowComparator;
    }


    public DataItemRow next() throws SQLException {
        return dataItemRowComparator.compareAndReturn(left.next(), right.next());
    }

}
