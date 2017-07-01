package org.the.force.jdbc.partition.resource.db;

import net.sf.json.JSONObject;
import org.the.force.jdbc.partition.exception.PartitionConfigException;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.resource.table.impl.LogicTableManagerImpl;
import org.the.force.jdbc.partition.rule.PartitionComparator;
import org.the.force.jdbc.partition.rule.config.DataNode;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Created by xuji on 2017/6/22.
 */
public class LogicDbManager implements LogicDbConfig {

    private static String PHYSIC_DBS_PATH = "physic_dbs";

    private static String LOGIC_TABLES_PATH = "logic_tables";

    private final DataNode logicDbNode;
    private final SqlDialect sqlDialect;
    private final String logicDbName;
    private final String paramStr;
    private final Properties info;

    private String actualDriverClassName;

    private final ConcurrentSkipListMap<String, PhysicDbConfig> physicDbConfigMap;

    private final Map<String, LogicTableManagerImpl> logicTableManagerMap;


    public LogicDbManager(DataNode logicDbNode, SqlDialect sqlDialect, String paramStr, Properties info) throws PartitionConfigException {
        this.logicDbNode = logicDbNode;
        this.sqlDialect = sqlDialect;
        this.logicDbName = logicDbNode.getKey();
        this.paramStr = paramStr;
        this.info = info;
        physicDbConfigMap = new ConcurrentSkipListMap<>(PartitionComparator.getSingleton());
        logicTableManagerMap = new ConcurrentHashMap<>(512);
        init();
    }

    private void init() throws PartitionConfigException {
        try {
            String json = logicDbNode.getData();
            JSONObject logicDbConfigJsonObject = JSONObject.fromObject(json);
            String actualDriverClassName = logicDbConfigJsonObject.getString("actualDriverClassName").trim();
            Class.forName(actualDriverClassName);
            this.actualDriverClassName = actualDriverClassName;
            DataNode physicDbsNode = logicDbNode.children(PHYSIC_DBS_PATH);
            List<DataNode> physicDbNodes = physicDbsNode.children();
            for (DataNode physicDb : physicDbNodes) {
                String physicDbName = physicDb.getKey();//原生的value
                PhysicDbConfigImpl physicDbConfig = new PhysicDbConfigImpl(physicDb, physicDbName);
                this.putPhysicDbConfig(physicDbName.toLowerCase(), physicDbConfig);
            }

            DataNode logicTablesNode = logicDbNode.children(LOGIC_TABLES_PATH);
            List<DataNode> logicTables = logicTablesNode.children();
            for (DataNode logicTableNode : logicTables) {
                String logicTableName = logicTableNode.getKey().toLowerCase();
                LogicTableManagerImpl logicTableManager = new LogicTableManagerImpl(sqlDialect, logicTableName, logicTableNode);
                logicTableManagerMap.put(logicTableName, logicTableManager);
            }
        } catch (Exception e) {
            if (e instanceof PartitionConfigException) {
                throw (PartitionConfigException) e;
            } else {
                throw new PartitionConfigException("", e);
            }
        }
    }


    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }

    public String getLogicDbName() {
        return logicDbName;
    }

    public String getParamStr() {
        return paramStr;
    }

    public Properties getInfo() {
        return info;
    }


    public void putPhysicDbConfig(String physicDbName, PhysicDbConfig physicDbConfig) throws PartitionConfigException {
        physicDbConfigMap.put(physicDbName.toLowerCase(), physicDbConfig);
    }

    public PhysicDbConfig removePhysicDbConfig(String physicDbName) {
        return physicDbConfigMap.remove(physicDbName.toLowerCase());
    }

    public PhysicDbConfig getPhysicDbConfig(String physicDbName) {
        return physicDbConfigMap.get(physicDbName.toLowerCase());
    }



    public int getPhysicDbSize() {
        return physicDbConfigMap.size();
    }

    public void putLogicTableManager(String logicTableCName, LogicTableManagerImpl logicTableConfig) throws PartitionConfigException {
        logicTableManagerMap.put(logicTableCName.toLowerCase(), logicTableConfig);
    }

    public LogicTableManagerImpl getLogicTableManager(String logicTableName) {
        return logicTableManagerMap.get(logicTableName.toLowerCase());
    }

    public Map<String, PhysicDbConfig> getPhysicDbConfigMap() {
        return physicDbConfigMap;
    }


    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        LogicDbManager that = (LogicDbManager) o;
        return getLogicDbName().equals(that.getLogicDbName());
    }

    public int hashCode() {
        return getLogicDbName().hashCode();
    }


    public String getActualDriverClassName() {
        return actualDriverClassName;
    }

    public Map<String, LogicTableManagerImpl> getLogicTableManagerMap() {
        return logicTableManagerMap;
    }
}
