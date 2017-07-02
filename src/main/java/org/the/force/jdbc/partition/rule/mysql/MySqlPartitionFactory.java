package org.the.force.jdbc.partition.rule.mysql;

import org.the.force.jdbc.partition.common.json.JsonParser;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionFactory;
import org.the.force.jdbc.partition.rule.PartitionRule;
import org.the.force.jdbc.partition.rule.config.DataNode;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuji on 2017/6/30.
 */
public class MySqlPartitionFactory implements PartitionFactory {

    private Map<PartitionRule.RuleType, Class<? extends MySqlPartition>> implClassMap = new HashMap<>();

    public MySqlPartitionFactory() {
        implClassMap.put(PartitionRule.RuleType.None, NoPartition.class);
        implClassMap.put(PartitionRule.RuleType.DB, PhysicDbPartition.class);
        implClassMap.put(PartitionRule.RuleType.TABLE_IN_DB, PhysicDbTablePartition.class);
        implClassMap.put(PartitionRule.RuleType.TABLE, PhysicTablePartition.class);
    }

    public Partition buildPartition(PartitionRule.RuleType partitionType, DataNode partitionNode) throws Exception {
        String json = partitionNode.getData();
        Map<String, Object> partitionJsonObject = new JsonParser(json).parse();
        Class<? extends MySqlPartition> clazz = implClassMap.get(partitionType);
        Constructor<? extends MySqlPartition> c = clazz.getDeclaredConstructor(String.class, String.class);
        Partition partition = c.newInstance(partitionJsonObject.get("physicTableName").toString().trim(), partitionJsonObject.get("physicDbName").toString().trim());
        return partition;
    }
}
