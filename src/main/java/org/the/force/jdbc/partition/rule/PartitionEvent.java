package org.the.force.jdbc.partition.rule;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

/**
 * Created by xuji on 2017/6/1.
 * 分库分表事件信息，用于选择分区
 * 每个事件只描述一个逻辑表的信息，只用于对一个逻辑表选择分区
 * 注意，为了保证通过数据库Driver的url就可以访问和操作逻辑数据库，数据库所有的配置信息都同步到zk上，因此partitionEvent携带了数据库配置信息
 */
public class PartitionEvent {

    private final String logicTableName;

    private final EventType eventType;

    /**
     * key为columnName,一个column可以有多个分库分表的功能
     */
    private final Map<String, Set<PartitionColumnConfig>> partitionColumnConfigMap;

    private SortedSet<Partition> partitions;

    private SortedSet<String> physicDbs;


    public PartitionEvent(String logicTableName, EventType eventType, Map<String, Set<PartitionColumnConfig>> partitionColumnConfigMap) {
        this.logicTableName = logicTableName;
        this.eventType = eventType;
        this.partitionColumnConfigMap = partitionColumnConfigMap;
    }

    public String getLogicTableName() {
        return logicTableName;
    }


    public EventType getEventType() {
        return eventType;
    }

    public Set<PartitionColumnConfig> getPartitionColumnConfig(String columnName) {
        return partitionColumnConfigMap.get(columnName.toLowerCase());
    }

    public SortedSet<Partition> getPartitions() {
        return partitions;
    }

    public void setPartitions(SortedSet<Partition> partitions) {
        this.partitions = partitions;
    }

    public SortedSet<String> getPhysicDbs() {
        return physicDbs;
    }

    public void setPhysicDbs(SortedSet<String> physicDbs) {
        this.physicDbs = physicDbs;
    }

    public static enum EventType {
        DDL,
        INSERT,
        UPDATE,
        DELETE,
        SELECT,
    }


}
