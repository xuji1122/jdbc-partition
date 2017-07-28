package org.the.force.jdbc.partition.test;

import java.util.TreeSet;

/**
 * Created by xuji on 2017/7/27.
 */
public class IdStock {

    private TreeSet<Long> idTree = new TreeSet<>();

    public IdStock(long number) {
        for (long i = 1; i <= number; i++) {
            idTree.add(i);
        }
    }

    public IdStock(long[] numbers) {
        for (int i = 0; i <= numbers.length; i++) {
            idTree.add(numbers[i]);
        }
    }

    public synchronized Long nextId() {
        return idTree.pollFirst();
    }
}
