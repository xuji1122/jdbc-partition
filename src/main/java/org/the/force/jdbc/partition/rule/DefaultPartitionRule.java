package org.the.force.jdbc.partition.rule;

import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import org.the.force.jdbc.partition.common.tuple.Triple;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by xuji on 2017/5/16.
 */
public class DefaultPartitionRule implements PartitionRule {

    private final HashFunction hashFunction = Hashing.murmur3_128();

    public SortedSet<Partition> selectPartitions(PartitionEvent partitionEvent, SortedSet<PartitionColumnValue> partitionColumnValueSet) {

        SortedSet<Partition> returnPartitions = selectFromPartitions(partitionEvent, partitionColumnValueSet, partitionEvent.getPhysicDbs(), partitionEvent.getPartitions());
        return returnPartitions;
    }

    protected SortedSet<Partition> selectFromPartitions(PartitionEvent partitionEvent, SortedSet<PartitionColumnValue> partitionColumnValueSet, SortedSet<String> physicDbs,
        SortedSet<Partition> partitions) {
        TreeSet<Partition> dbPartitions = new TreeSet<>(partitionEvent.getPartitionSortType().getComparator());
        //======分库路由===

        LinkedList<Triple<PartitionColumnValue, PartitionColumnConfig, Long>> partitionRuleAndValues = new LinkedList<>();
        for (PartitionColumnValue partitionColumnValue : partitionColumnValueSet) {
            String columnName = partitionColumnValue.getColumnName();
            Object value = partitionColumnValue.getValue();
            Set<PartitionColumnConfig> set = partitionEvent.getPartitionColumnConfig(columnName);
            for (PartitionColumnConfig partitionColumnConfig : set) {
                Long val = getNumber(value, partitionColumnConfig.getValueFromIndex(), partitionColumnConfig.getValueToIndex());
                if (val == null) {
                    continue;
                }
                Triple<PartitionColumnValue, PartitionColumnConfig, Long> triple = new Triple(partitionColumnValue, partitionColumnConfig, val);
                if (partitionColumnConfig.getPartitionRuleType() == RuleType.TABLE) {
                    //优先判断此类分库且分表的列
                    //一次性分库并分表 严格按照表格定义分区，物理库是关联属性
                    TreeSet<Partition> one = new TreeSet<>(partitionEvent.getPartitionSortType().getComparator());
                    one.add(doOnePartitionSelect(val, partitions));
                    return one;
                } else if (partitionColumnConfig.getPartitionRuleType() == RuleType.DB) {
                    int to = (int) (val % physicDbs.size());
                    int index = 0;
                    Iterator<String> iterator = physicDbs.iterator();
                    String physicDbName = null;
                    while (iterator.hasNext()) {
                        String p = iterator.next();
                        if (index == to) {
                            physicDbName = p;
                            break;
                        }
                        index++;
                    }
                    Iterator<Partition> partitionIterator = partitions.iterator();
                    while (partitionIterator.hasNext()) {
                        Partition partition = partitionIterator.next();
                        if (partition.getPhysicDbName().equalsIgnoreCase(physicDbName)) {
                            dbPartitions.add(partition);
                        }
                    }
                    if (dbPartitions.isEmpty()) {
                        //TODO 异常
                    }
                } else {
                    //备用
                    partitionRuleAndValues.add(triple);
                }
            }
        }
        if (dbPartitions.size() == 1) {//分库不分表
            return dbPartitions;
        } else if (dbPartitions.size() == 0) {// 没有匹配到库
            //如果有分表的规则存在则尽量做分区  返回所有的可能性组合
            if (partitionRuleAndValues.isEmpty()) {
                return dbPartitions;
            }
            //遍历数据库分表
            if (partitionEvent.getEventType() == PartitionEvent.EventType.INSERT || partitionEvent.getEventType() == PartitionEvent.EventType.DDL) {
                return dbPartitions;
            }
            TreeSet<Partition> returnPartitions = new TreeSet<>(partitionEvent.getPartitionSortType().getComparator());
            Map<String, List<Partition>> map = partitions.stream().collect(Collectors.groupingBy(Partition::getPhysicDbName, Collectors.toList()));
            for (Triple<PartitionColumnValue, PartitionColumnConfig, Long> partitionValue : partitionRuleAndValues) {
                if (partitionValue.getMiddle().getPartitionRuleType() == RuleType.TABLE_IN_DB) {
                    for (List<Partition> list : map.values()) {
                        TreeSet<Partition> set = new TreeSet<>(partitionEvent.getPartitionSortType().getComparator());
                        set.addAll(list);
                        returnPartitions.add(doOnePartitionSelect(partitionValue.getRight(), set));
                    }
                } else {
                    //TODO 异常
                }
            }
            return returnPartitions;
        } else {
            //先分库 再分表
            for (Triple<PartitionColumnValue, PartitionColumnConfig, Long> shardingValue : partitionRuleAndValues) {
                if (shardingValue.getMiddle().getPartitionRuleType() == RuleType.TABLE_IN_DB) {
                    TreeSet<Partition> set = new TreeSet<>(partitionEvent.getPartitionSortType().getComparator());
                    set.add(doOnePartitionSelect(shardingValue.getRight(), dbPartitions));
                    return set;
                }
            }
            return dbPartitions;
        }

    }

    protected Partition doOnePartitionSelect(long val, SortedSet<Partition> partitions) {
        int to = (int) (val % partitions.size());
        Iterator<Partition> partitionIterator = partitions.iterator();
        int index = 0;
        while (partitionIterator.hasNext()) {
            Partition partition = partitionIterator.next();
            if (index == to) {
                return partition;
            }
            index++;
        }
        return null;
    }

    protected Long getNumber(Object value, int valueFromIndex, int valueToIndex) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            String str = (String) value;
            if (valueFromIndex < 0) {
                return hashFunction.hashString(str, Charsets.ISO_8859_1).asLong();
            }
            if (valueToIndex < 0 || valueToIndex < valueFromIndex) {
                valueToIndex = str.length();
            }
            String sub = str.substring(valueFromIndex, valueToIndex);
            return Long.parseLong(sub);
        }
        return null;
    }
}
