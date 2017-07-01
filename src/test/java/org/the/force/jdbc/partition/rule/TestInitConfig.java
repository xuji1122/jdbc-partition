package org.the.force.jdbc.partition.rule;

import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcPartitionBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xuji on 2017/5/16.
 */
@Test(priority = 200)
public class TestInitConfig extends TestJdbcPartitionBase {

    private Logger logger = LoggerFactory.getLogger(TestInitConfig.class);

    private String zkPath = "/" + zkRootPath + "/";

    private String LOGIC_DB_PATH = zkPath + logicDbName;

    private String DB_PATH = zkPath + logicDbName + "/physic_dbs";

    private String TABLE_PATH = zkPath + logicDbName + "/logic_tables";

    private static String OLD_PATH = "old";

    private static String NEW_PATH = "new";

    BackgroundCallback backgroundCallback;

    CuratorFramework curatorFramework;

    public TestInitConfig(){
        backgroundCallback = (client, event) -> logger.info("crete-result:{}", event.getType().name());
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework = CuratorFrameworkFactory.builder().connectString(zkConnectStr).connectionTimeoutMs(15000).sessionTimeoutMs(20000).retryPolicy(retryPolicy).build();
        curatorFramework.start();
    }

    @Test(priority = 201)
    public void setDb() throws Exception {
        createNode(LOGIC_DB_PATH);
        createNode(DB_PATH);
        createNode(TABLE_PATH);
        setDbData(logicDbName);
    }

    private void setDbData(String logicDbName) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("actualDriverClassName", actualDriverClassName);
        byte[] data = JSONObject.fromObject(map).toString().getBytes("UTF-8");
        Stat stat = curatorFramework.setData().forPath(LOGIC_DB_PATH, data);
        logger.info(stat.toString());
        for (int i = 0; i < 2; i++) {
            map = new HashMap<>();
            map.put("url", defaultPhysicDbConnectionUrlPrefix + logicDbName + "_" + i);
            data = JSONObject.fromObject(map).toString().getBytes("UTF-8");
            String physicDbPath = DB_PATH + "/" + logicDbName + "_" + i;
            createNode(physicDbPath);
            stat = curatorFramework.setData().forPath(physicDbPath, data);
            logger.info("setDbData:{}", stat.toString());
        }
    }

    @Test(priority = 202)
    public void setOldTableData() throws Exception {
        setTableData(logicDbName, OLD_PATH, "t_user");
        setTableData(logicDbName, OLD_PATH, "t_order");
        setTableData(logicDbName, OLD_PATH, "t_order_sku");
    }

    private void setTableData(String logicDbName, String path, String logicTableName) throws Exception {
        logicTableName = logicTableName.toLowerCase();
        List<Object> partitionColumnConfigs = new ArrayList();
        for (int i = 0; i < 1; i++) {
            if (i == 0) {
                Map<String, Object> config = new HashedMap();
                config.put("partitionColumnName", "id");
                Set<PartitionColumnConfig> set = new HashSet<>();
                PartitionColumnConfig columnConfig = new PartitionColumnConfig(-1, -1, PartitionRule.RuleType.TABLE);
                set.add(columnConfig);
                config.put("configs", set);
                partitionColumnConfigs.add(config);
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("partitionRuleType", PartitionRule.RuleType.TABLE.name());
        map.put("partitionColumnConfigs", partitionColumnConfigs);
        String json = JSONObject.fromObject(map).toString();
        byte[] data = json.getBytes("UTF-8");
        String logicTablePath = TABLE_PATH + "/" + logicTableName + "/" + path;
        createNode(logicTablePath);
        Stat stat = curatorFramework.setData().forPath(logicTablePath, data);
        logger.info("setTableData:{}", stat.toString());
        for (int i = 0; i < 8; i++) {
            map = new HashMap<>();
            map.put("physicDbName", logicDbName + "_" + (i / 4));
            map.put("physicTableName", logicTableName + "_" + i);
            data = JSONObject.fromObject(map).toString().getBytes("UTF-8");
            String physicTablePath = logicTablePath + "/" + logicTableName + "_" + i;
            createNode(physicTablePath);
            stat = curatorFramework.setData().forPath(physicTablePath, data);
            logger.info(stat.toString());
        }
    }

    private void createNode(String path) throws Exception {
        try {
            Stat stat = curatorFramework.checkExists().forPath(path);
            if (stat == null) {
                String result = curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path, null);
                logger.info("path={},result={}", path, result);
            }
        } catch (KeeperException.NodeExistsException e) {

        }
    }

    @Test(priority = 203)
    public void test1() throws Exception {
        Class.forName("org.the.force.jdbc.partition.driver.JdbcPartitionDriver");
        Connection connection = DriverManager.getConnection(dbConnectionUrl, user, password);
        connection.close();
    }
}
