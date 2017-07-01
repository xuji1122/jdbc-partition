package org.the.force.jdbc.partition.rule.hits;

import org.the.force.jdbc.partition.rule.PartitionColumnValue;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Created by xuji on 2017/5/18.
 * 必须按照 remove set get 的顺序调用
 */
public class HitManager {



    private static ThreadLocal<Map<PartitionColumn, Object>> threadLocal = new ThreadLocal<>();

    public static void set(Map<PartitionColumn, Object> map) {
        if (threadLocal.get() != null) {
            throw new HitManagerException("HitManager Map is not null,please remove it first");
        }
        threadLocal.set(map);

    }

    public static Map<PartitionColumn, Object> get() {
        Map<PartitionColumn, Object> map = threadLocal.get();
        if (map == null) {
            throw new HitManagerException("HitManager Map is null,please set it first");
        }
        return map;
    }

    public static void remove() {
        threadLocal.remove();
    }

    public static boolean isNull() {
        return threadLocal.get() == null;
    }



    public static TreeSet<PartitionColumnValue> match(String logicTableName) {
        Map<PartitionColumn, Object> map = threadLocal.get();
        if (map == null) {
            return new TreeSet<>();
        }
        return map.entrySet().parallelStream().filter(entry -> entry.getKey().getLogicTableName().equalsIgnoreCase(logicTableName))
            .map(entry -> new PartitionColumnValue() {
                public String getColumnName() {
                     return entry.getKey().getColumnName();
                }

                public Object getValue() {
                    return entry.getValue();
                }

                public int compareTo(PartitionColumnValue o) {
                    return this.getColumnName().compareTo(o.getColumnName());
                }
            }).collect(new CollectorImpl<>((Supplier<TreeSet<PartitionColumnValue>>) TreeSet::new, Set::add,
                (left, right) -> { left.addAll(right); return left; },
                CollectorImpl.CH_UNORDERED_ID));
    }


    static class CollectorImpl<T, A, R> implements Collector<T, A, R> {

        static final Set<Collector.Characteristics> CH_UNORDERED_ID
            = Collections.unmodifiableSet(EnumSet.of(Collector.Characteristics.UNORDERED,
            Collector.Characteristics.IDENTITY_FINISH));
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;

        CollectorImpl(Supplier<A> supplier,
            BiConsumer<A, T> accumulator,
            BinaryOperator<A> combiner,
            Function<A,R> finisher,
            Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }

        CollectorImpl(Supplier<A> supplier,
            BiConsumer<A, T> accumulator,
            BinaryOperator<A> combiner,
            Set<Characteristics> characteristics) {
            this(supplier, accumulator, combiner, castingIdentity(), characteristics);
        }

        @Override
        public BiConsumer<A, T> accumulator() {
            return accumulator;
        }

        @Override
        public Supplier<A> supplier() {
            return supplier;
        }

        @Override
        public BinaryOperator<A> combiner() {
            return combiner;
        }

        @Override
        public Function<A, R> finisher() {
            return finisher;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return characteristics;
        }

        @SuppressWarnings("unchecked")
        private static <I, R> Function<I, R> castingIdentity() {
            return i -> (R) i;
        }
    }

}
