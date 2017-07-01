package org.the.force.jdbc.partition.engine.result;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by xuji on 2017/5/29.
 */
public class UpdateMerger {

    private final Map<Integer, AtomicInteger> resultMap;

    private final Map<Integer, Integer> failedLineNum = new ConcurrentHashMap<>();

    public UpdateMerger(int totalLineNumber) {
        resultMap = new HashMap<>(totalLineNumber);
        for (int i = 0; i < totalLineNumber; i++) {
            resultMap.put(i, new AtomicInteger(0));
        }
    }

    public int addSuccess(int lineNum, int affectRows) {
        AtomicInteger r = resultMap.get(lineNum);
        if (r == null) {
            //TODO
            return 0;
        }
        return r.addAndGet(affectRows);
    }

    public void addFailed(int lineNum, int resultCode) {
        failedLineNum.put(lineNum, resultCode);
    }

    public int[] toArray() {
        int size = resultMap.size();
        int[] result = new int[size];
        for (int i = 0; i < size; i++) {
            if (failedLineNum.get(i) != null) {
                result[i] = failedLineNum.get(i);
            } else {
                result[i] = this.resultMap.get(i).intValue();
            }
        }
        return result;
    }

    public int toInt() {
        int size = resultMap.size();
        int total = 0;
        for (int i = 0; i < size; i++) {
            total += this.resultMap.get(i).intValue();
        }
        return total;
    }


}
