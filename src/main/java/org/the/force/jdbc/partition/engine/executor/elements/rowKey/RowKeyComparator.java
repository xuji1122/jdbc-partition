package org.the.force.jdbc.partition.engine.executor.elements.rowKey;

import java.util.Comparator;

/**
 * Created by xuji on 2017/6/8.
 */
public class RowKeyComparator implements Comparator<Object> {

    private boolean asc;

    public int compare(Object o1, Object o2) {
        if (o1 instanceof String && o2 instanceof String) {
            return ((String) o1).compareTo((String) o2);
        } else if (o1 instanceof StringsRowKey && o2 instanceof StringsRowKey) {
            String[] left = ((StringsRowKey) o1).getValue();
            String[] right = ((StringsRowKey) o2).getValue();
            for (int i = 0; i < left.length; i++) {
                int result = left[i].compareTo(right[i]);
                if (result != 0) {
                    return result;
                }
            }
        }
        return 0;
    }
}
