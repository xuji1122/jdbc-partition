package org.the.force.jdbc.partition.rule;

import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestSupport;

import java.sql.Connection;

/**
 * Created by xuji on 2017/5/16.
 */
@Test(priority = 100)
public class TestZookeeperConfig {

    CuratorFramework curatorFramework = TestSupport.partitionDb.getZk();

    public TestZookeeperConfig() {

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
    public void testConnectDriver() throws Exception {
        Class.forName("org.the.force.jdbc.partition.driver.JdbcPartitionDriver");
        Connection connection = TestSupport.partitionDb.getConnection();
        connection.close();
    }

}
