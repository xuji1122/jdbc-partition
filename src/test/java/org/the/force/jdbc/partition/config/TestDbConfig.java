package org.the.force.jdbc.partition.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestSupport;

import java.sql.Connection;

/**
 * Created by xuji on 2017/5/16.
 */
@Test(priority = 100)
public class TestDbConfig {

    CuratorFramework curatorFramework = TestSupport.partitionDb.getZk();

    public TestDbConfig() {

    }

    @Test(priority = 50)
    public void initConfig() throws Exception {
        try {
            curatorFramework.delete().deletingChildrenIfNeeded().forPath("/" + TestSupport.partitionDb.logicDbName);
        } catch (KeeperException.NoNodeException exception) {

        }
        TestSupport.partitionDb.jsonDbDataNode.writeToZk(curatorFramework);
    }

    @Test(priority = 51)
    public void testZkConnectDriver() throws Exception {
        Class.forName("org.the.force.jdbc.partition.driver.JdbcPartitionDriver");
        Connection connection = TestSupport.partitionDb.getConnection();
        connection.close();
    }

    @Test(priority = 51)
    public void testYmFileConnectDriver() throws Exception {
        Class.forName("org.the.force.jdbc.partition.driver.JdbcPartitionDriver");
        String dbConnectionUrl =
            "jdbc:partition:" + TestSupport.sqlDialectName + "@file_yml://" + TestSupport.test_cases_basic_path + "/schema/" + TestSupport.partitionDb.logicDbName + ".yml"
                + "?characterEncoding=utf-8&allowMultiQueries=true&cachePrepStmts=true&useServerPrepStmts=false";
        System.setProperty("org.the.force.jdbc.partition.config.protocol.file_yml.impl", YmlFileConfigUrl.class.getName());
        Connection connection = TestSupport.partitionDb.getConnection(dbConnectionUrl);
        connection.close();
    }

}
