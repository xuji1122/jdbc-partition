package org.the.force.jdbc.partition.resource.db;

import org.the.force.jdbc.partition.common.json.JsonParser;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.exception.PartitionConfigException;
import org.the.force.jdbc.partition.exception.SqlParseException;
import org.the.force.jdbc.partition.resource.table.impl.LogicTableManagerImpl;
import org.the.force.jdbc.partition.rule.PartitionComparator;
import org.the.force.jdbc.partition.rule.config.DataNode;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;
import org.the.force.thirdparty.druid.util.JdbcUtils;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by xuji on 2017/6/22.
 */
public class LogicDbManager implements LogicDbConfig {

    private static Log logger = LogFactory.getLog(LogicDbManager.class);

    private static String PHYSIC_DBS_PATH = "physic_dbs";

    private static String LOGIC_TABLES_PATH = "logic_tables";

    private final DataNode logicDbNode;
    private final SqlDialect sqlDialect;
    private final String logicDbName;
    private final String paramStr;
    private final Properties info;

    private String actualDriverClassName;

    private final ConcurrentSkipListMap<String, PhysicDbConfig> physicDbConfigMap;

    private final ConcurrentSkipListMap<String, LogicTableManagerImpl> logicTableManagerMap;


    public LogicDbManager(DataNode logicDbNode, SqlDialect sqlDialect, String paramStr, Properties info) throws SQLException {
        this.logicDbNode = logicDbNode;
        this.sqlDialect = sqlDialect;
        this.logicDbName = logicDbNode.getKey();
        this.paramStr = paramStr;
        this.info = info;
        physicDbConfigMap = new ConcurrentSkipListMap<>(PartitionComparator.getSingleton());
        logicTableManagerMap = new ConcurrentSkipListMap<>(PartitionComparator.getSingleton());
        init();
    }

    private void init() throws SQLException  {
        try {
            String json = logicDbNode.getData();
            JsonParser parser = new JsonParser(json);
            Map<String, Object> map = parser.parse();
            String actualDriverClassName = map.get("actualDriverClassName").toString().trim();
            this.actualDriverClassName = actualDriverClassName;
            JdbcUtils.createDriver(this.actualDriverClassName);
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
                LogicTableManagerImpl logicTableManager = new LogicTableManagerImpl(this, logicTableName, logicTableNode);
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

    public void loadDbMetaData() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        CountDownLatch countDownLatch = new CountDownLatch(logicTableManagerMap.size());
        for (Map.Entry<String, LogicTableManagerImpl> entry : logicTableManagerMap.entrySet()) {
            executorService.submit(() -> {
                try {
                    entry.getValue().initDbMetaData();
                } catch (Exception e) {
                    logger.error(MessageFormat.format("{0} initDbMetaData failed", entry.getKey()), e);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        executorService.shutdown();
        countDownLatch.await();
        while (!executorService.isTerminated()) {
            Thread.sleep(20);
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

    public String getActualDriverClassName() {
        return actualDriverClassName;
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
        LogicTableManagerImpl logicTableManager = logicTableManagerMap.get(logicTableName.toLowerCase());
        if (logicTableManager == null) {
            throw new SqlParseException("getLogicTableManager is null");
        }
        return logicTableManager;
    }

    public Map<String, PhysicDbConfig> getPhysicDbConfigMap() {
        return physicDbConfigMap;
    }


    public SortedSet<String> getLogicTables() {
        return logicTableManagerMap.keySet();
    }

    public Map<String, LogicTableManagerImpl> getLogicTableManagerMap() {
        return logicTableManagerMap;
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



}
