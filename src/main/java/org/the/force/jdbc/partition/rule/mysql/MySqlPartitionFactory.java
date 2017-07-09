package org.the.force.jdbc.partition.rule.mysql;

import org.the.force.jdbc.partition.common.json.JsonParser;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionFactory;
import org.the.force.jdbc.partition.rule.config.DataNode;

import java.util.Map;

/**
 * Created by xuji on 2017/6/30.
 */
public class MySqlPartitionFactory implements PartitionFactory {

    public MySqlPartitionFactory() {

    }

    public Partition buildPartition(DataNode partitionNode) throws Exception {
        String json = partitionNode.getData();
        Map<String, Object> partitionJsonObject = new JsonParser(json).parse();
        return new MySqlPartition(partitionJsonObject.get("physicTableName").toString().trim(), partitionJsonObject.get("physicDbName").toString().trim());
    }
}
