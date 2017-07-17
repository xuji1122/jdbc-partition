package org.the.force.jdbc.partition.engine.executor.result;

import org.the.force.thirdparty.druid.sql.ast.SQLOrderingSpecification;

import java.util.Comparator;
import java.util.List;

/**
 * Created by xuji on 2017/6/6.
 */
public class DataItemRowComparator implements Comparator<DataItemRow> {

    protected final List<OrderByItem> orderByItems;

    public DataItemRowComparator(List<OrderByItem> orderByItems) {
        this.orderByItems = orderByItems;
    }

    public int compare(DataItemRow o1, DataItemRow o2) {
        for (int i = 0; i < orderByItems.size(); i++) {
            OrderByItem item = orderByItems.get(i);
            Object v1 = o1.getValue(item.getItemIndex());
            Object v2 = o2.getValue(item.getItemIndex());
            if (v1 instanceof Comparable<?> && v2 instanceof Comparable<?>) {
                Comparable<Object> c1 = (Comparable<Object>) v1;
                Comparable<Object> c2 = (Comparable<Object>) v2;
                int result = c1.compareTo(c2);
                if (result == 0) {

                    continue;
                }
                if (item.getOrderByType() == SQLOrderingSpecification.ASC) {
                    return result;
                } else {
                    return result * (-1);
                }
            } else {
                //TODO 异常
            }
        }
        //ROW rowkey 相等
        return 0;
    }

    public DataItemRow compareAndReturn(DataItemRow leftRow, DataItemRow rightRow) {
        if (leftRow == null) {
            return rightRow;
        } else if (rightRow == null) {
            return leftRow;
        }
        //compare
        int result = this.compare(leftRow, rightRow);
        if (result < 0) {
            return leftRow;
        } else {
            return rightRow;
        }
    }
}
