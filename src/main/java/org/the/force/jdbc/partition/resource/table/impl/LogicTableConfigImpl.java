package org.the.force.jdbc.partition.resource.table.impl;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.the.force.jdbc.partition.exception.PartitionConfigException;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.Partition;
import org.the.force.jdbc.partition.rule.PartitionColumnConfig;
import org.the.force.jdbc.partition.rule.PartitionComparator;
import org.the.force.jdbc.partition.rule.PartitionRule;
import org.the.force.jdbc.partition.rule.config.DataNode;
import org.the.force.jdbc.partition.rule.DefaultPartitionRule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by xuji on 2017/5/16.
 */
public class LogicTableConfigImpl implements LogicTableConfig {

    private final SqlDialect sqlDialect;

    private final String logicTableName;

    private final PartitionRule.RuleType partitionType;

    private final PartitionRule partitionRule;

    private final long version;

    private final ConcurrentSkipListMap<String, Set<PartitionColumnConfig>> partitionColumnConfigs = new ConcurrentSkipListMap<>();

    private final SortedSet<Partition> partitions = new ConcurrentSkipListSet<>();

    private final SortedSet<String> physicDbs = new ConcurrentSkipListSet<>(PartitionComparator.getSingleton());

    public LogicTableConfigImpl(SqlDialect sqlDialect, String logicTableName, DataNode logicTableNode) throws Exception {
        this.sqlDialect = sqlDialect;
        this.logicTableName = logicTableName;
        String json = logicTableNode.getData();
        JSONObject logicTableJsonObject = JSONObject.fromObject(json);
        PartitionRule.RuleType partitionType = PartitionRule.RuleType.TABLE;
        if (logicTableJsonObject.has("partitionRuleType")) {
            PartitionRule.RuleType d = PartitionRule.RuleType.valueOfString(logicTableJsonObject.getString("partitionRuleType"));
            if (d == null) {
                //TODO 异常处理
            }
            partitionType = d;
        }
        this.partitionType = partitionType;
        PartitionRule partitionRule = new DefaultPartitionRule();
        if (logicTableJsonObject.has("partitionRuleClassName")) {
            String partitionRuleClassName = logicTableJsonObject.getString("partitionRuleClassName");
            if (partitionRuleClassName != null && partitionRuleClassName.trim().length() > 0) {
                Class clazz = Class.forName(partitionRuleClassName.trim());
                Object obj = clazz.newInstance();
                if (!(obj instanceof PartitionRule)) {
                    throw new PartitionConfigException("partitionRuleClass:" + partitionRuleClassName + " 必须实现 " + PartitionRule.class.getName() + "接口");
                }
                partitionRule = (PartitionRule) obj;
            }
        }
        this.partitionRule = partitionRule;
        long version = -1;
        if (logicTableJsonObject.has("version")) {
            version = Long.parseLong(logicTableJsonObject.getString("version").trim());
        }
        this.version = version;
        JSONArray jsonArray = logicTableJsonObject.getJSONArray("partitionColumnConfigs");//大小写不敏感
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject element = (JSONObject) jsonArray.get(i);
            String partitionColumnName = element.getString("partitionColumnName");
            JSONArray configs = element.getJSONArray("configs");
            Set<PartitionColumnConfig> partitionColumnConfigSet = new HashSet<>();
            for (int k = 0; k < configs.size(); k++) {
                JSONObject config = (JSONObject) configs.get(k);
                PartitionRule.RuleType partitionRuleType = PartitionRule.RuleType.valueOfString(config.getString("partitionRuleType"));
                PartitionColumnConfig columnConfig = new PartitionColumnConfig(config.getInt("valueFromIndex"), config.getInt("valueToIndex"), partitionRuleType);
                partitionColumnConfigSet.add(columnConfig);
            }
            putPartitionColumnConfig(partitionColumnName, partitionColumnConfigSet);
        }
        init(logicTableNode);
    }

    protected void init(DataNode logicTableNode) throws Exception {

        List<DataNode> partitionNodes = logicTableNode.children();
        for (int i = 0; i < partitionNodes.size(); i++) {
            DataNode partitionNode = partitionNodes.get(i);
            Partition partition = sqlDialect.getPartitionFactory().buildPartition(partitionType, partitionNode);
            addPartition(partition);
        }
    }

    public synchronized void addPartition(Partition partition) throws PartitionConfigException {
        partitions.add(partition);
        physicDbs.add(partition.getPhysicDbName());
    }

    public void putPartitionColumnConfig(String columnName, Set<PartitionColumnConfig> columnConfigs) {
        partitionColumnConfigs.put(columnName.toLowerCase(), columnConfigs);
    }


    public ConcurrentSkipListMap<String, Set<PartitionColumnConfig>> getPartitionColumnConfigs() {
        return partitionColumnConfigs;
    }

    public String getLogicTableName() {
        return logicTableName;
    }

    public PartitionRule getPartitionRule() {
        return partitionRule;
    }

    public SortedSet<String> getPartitionColumnNames() {
        return partitionColumnConfigs.keySet();
    }

    public SortedSet<Partition> getPartitions() {
        return partitions;
    }

    public SortedSet<String> getPhysicDbs() {
        return physicDbs;
    }

    public long getVersion() {
        return version;
    }

    public PartitionRule.RuleType getPartitionType() {
        return partitionType;
    }
}
