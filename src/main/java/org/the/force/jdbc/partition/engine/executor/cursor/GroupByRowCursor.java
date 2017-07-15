package org.the.force.jdbc.partition.engine.executor.cursor;

import org.the.force.jdbc.partition.engine.result.DataItemRow;
import org.the.force.jdbc.partition.engine.result.DataItemRowComparator;
import org.the.force.jdbc.partition.engine.result.RowCursor;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * Created by xuji on 2017/6/6.
 */
public class GroupByRowCursor extends ComparableRowCursor {

    private final DataItemRow[] temp = new DataItemRow[] {DataItemRow.EMPTY_DATA_ROW, DataItemRow.EMPTY_DATA_ROW};

    protected final DataItemRowComparator dataItemRowComparator;

    // SQLAggregateExpr
    public GroupByRowCursor(RowCursor left, RowCursor right,int[] sqlTypes, Map<String,Integer> resultSetMetaData, DataItemRowComparator dataItemRowComparator) {
        super(left, right,sqlTypes, resultSetMetaData);
        this.dataItemRowComparator = dataItemRowComparator;
    }

    public DataItemRow next() throws SQLException {
        if (temp[0] == DataItemRow.EMPTY_DATA_ROW) {
            temp[0] = left.next();
        }
        if (temp[1] == DataItemRow.EMPTY_DATA_ROW) {
            temp[1] = right.next();
        }
        //meger temp[0] temp[1]
        if (temp[0] == null) {
            return temp[1];
        }
        if (temp[1] == null) {
            return temp[0];
        }
        if (dataItemRowComparator == null) {
            //merge两行即可

        } else {
            //比较 key
            // key相同 合并 清空temp[0] temp[1]
            // key 不同 compare 排在前面的返回 另一个保留
            //
        }
        return null;
    }


}
