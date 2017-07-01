package org.the.force.jdbc.partition.rule;

/**
 * Created by xuji on 2017/6/1.
 * 分区字段的配置
 * 对象的scope是同一个逻辑表的同一个字段
 */
public class PartitionColumnConfig {
    /**
     * 字段分区的类型
     * 每个字段结合value可以筛选出一定范围的Partition
     * 字段级别的RuleType和table级别的RuleType是因果关系
     */
    private final PartitionRule.RuleType partitionRuleType;
    private final int valueFromIndex;
    private final int valueToIndex;

    public PartitionColumnConfig(int valueFromIndex, int valueToIndex, final PartitionRule.RuleType partitionRuleType) {
        this.valueFromIndex = valueFromIndex;
        this.valueToIndex = valueToIndex;
        this.partitionRuleType = partitionRuleType;
    }

    public int getValueFromIndex() {
        return valueFromIndex;
    }

    public int getValueToIndex() {
        return valueToIndex;
    }

    public PartitionRule.RuleType getPartitionRuleType() {
        return partitionRuleType;
    }



    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        PartitionColumnConfig that = (PartitionColumnConfig) o;

        if (getValueFromIndex() != that.getValueFromIndex())
            return false;
        return getValueToIndex() == that.getValueToIndex();

    }

    @Override
    public int hashCode() {
        int result = getValueFromIndex();
        result = 31 * result + getValueToIndex();
        return result;
    }
}
