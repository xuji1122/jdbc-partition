package org.the.force.jdbc.partition.rule;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcPartitionBase;
import org.the.force.jdbc.partition.common.BeanUtils;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.resource.db.LogicDbManager;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by xuji on 2017/5/16.
 */
@Test(priority = 200)
public class TestZookeeperConfig extends TestJdbcPartitionBase {

    private Log logger = LogFactory.getLog(TestZookeeperConfig.class);

    BackgroundCallback backgroundCallback;

    CuratorFramework curatorFramework;

    public TestZookeeperConfig() {
        backgroundCallback = (client, event) -> logger.info("crete-result:" + event.getType().name());
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        curatorFramework =
            CuratorFrameworkFactory.builder().namespace(zkRootPath).connectString(zkConnectStr).connectionTimeoutMs(15000).sessionTimeoutMs(20000).retryPolicy(retryPolicy).build();
        curatorFramework.start();
    }

    @Test(priority = 202)
    public void initConfig() throws Exception {
        super.jsonDbDataNode.writeToZk(curatorFramework);
    }



    @Test(priority = 203)
    public void testConnectDriver() throws Exception {
        Class.forName("org.the.force.jdbc.partition.driver.JdbcPartitionDriver");
        Connection connection = DriverManager.getConnection(dbConnectionUrl, user, password);
        connection.close();
    }


    @Test(priority = 201)
    public void testYmlConfig() throws Exception {
        LogicDbManager logicDbConfig = new LogicDbManager(jsonDbDataNode, SqlDialect.MySql, paramStr, propInfo);
        String json = BeanUtils.toJson(logicDbConfig);
        logger.info("1:\n" + json);
    }


}
