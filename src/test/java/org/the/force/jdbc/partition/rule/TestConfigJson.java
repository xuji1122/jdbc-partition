package org.the.force.jdbc.partition.rule;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcPartitionBase;
import org.the.force.jdbc.partition.common.BeanUtils;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.resource.db.LogicDbManager;
import org.the.force.jdbc.partition.rule.config.DataNode;
import org.the.force.jdbc.partition.rule.config.ZKDataNode;

/**
 * Created by xuji on 2017/7/2.
 */
@Test(priority = 300)
public class TestConfigJson extends TestJdbcPartitionBase {
    private Log logger = LogFactory.getLog(TestConfigJson.class);

    @Test(priority = 301)
    public void test2() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(500, 3);
        CuratorFramework curatorFramework =
            CuratorFrameworkFactory.builder().connectString(zkConnectStr).namespace("executor/" + sqlDialectName + "executor").connectionTimeoutMs(15000).sessionTimeoutMs(20000)
                .retryPolicy(retryPolicy).build();
        curatorFramework.start();
        DataNode zkDataNode = new ZKDataNode(null, logicDbName, curatorFramework);
        LogicDbManager logicDbConfig = new LogicDbManager(zkDataNode, SqlDialect.MySql, paramStr, propInfo);
        String json = BeanUtils.toJson(logicDbConfig);
        logger.info("1:\n" + json);
    }
}
