package org.the.force.jdbc.partition.engine.executor.cursor;

import org.the.force.jdbc.partition.engine.executor.result.DataItemRowComparator;
import org.the.force.jdbc.partition.engine.executor.result.RowCursor;
import org.the.force.jdbc.partition.engine.executor.result.DataItemRow;

import java.sql.SQLException;
import java.util.Map;

/**
 * Created by xuji on 2017/6/6.
 */
public class OrderByRowCursor extends ComparableRowCursor {

    protected final DataItemRowComparator dataItemRowComparator;

    public OrderByRowCursor(RowCursor left, RowCursor right,int[] sqlTypes, Map<String,Integer> resultSetMetaData, DataItemRowComparator dataItemRowComparator) {
        super(left, right,sqlTypes, resultSetMetaData);
        this.dataItemRowComparator = dataItemRowComparator;
    }


    public DataItemRow next() throws SQLException {
        return dataItemRowComparator.compareAndReturn(left.next(), right.next());
    }

}
