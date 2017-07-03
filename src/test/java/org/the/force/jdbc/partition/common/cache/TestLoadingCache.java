package org.the.force.jdbc.partition.common.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcPartitionBase;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.db.LogicDbManager;
import org.the.force.jdbc.partition.resource.sql.SqlPlanManager;
import org.the.force.jdbc.partition.rule.config.DataNode;
import org.the.force.jdbc.partition.rule.config.ZKDataNode;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

/**
 * Created by xuji on 2017/6/2.
 */
public class TestLoadingCache extends TestJdbcPartitionBase {

    private static Log logger = LogFactory.getLog(TestLoadingCache.class);

    @Test
    public void test1() throws Exception {
        LoadingCache<String, String> loadingCache =
            CacheBuilder.newBuilder().maximumSize(1024).concurrencyLevel(1024).initialCapacity(512).build(new CacheLoader<String, String>() {
                public String load(String key) throws Exception {
                    logger.info("load "+key);
                    return key + "_value";
                }
            });
        String value = loadingCache.get("123");
        Thread.sleep(1000);
        value = loadingCache.get("123");
        value = loadingCache.get("124");
        value = loadingCache.get("124");
        value = loadingCache.get("125");
        value = loadingCache.get("125");
        value = loadingCache.get("123");
    }

    @Test
    public void test2() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(500, 3);
        CuratorFramework curatorFramework =
            CuratorFrameworkFactory.builder().connectString(zkConnectStr).namespace(zkRootPath).connectionTimeoutMs(15000).sessionTimeoutMs(20000).retryPolicy(retryPolicy).build();
        curatorFramework.start();
        DataNode zkDataNode = new ZKDataNode(null, logicDbName, curatorFramework);
        LogicDbConfig logicDbConfig = new LogicDbManager(zkDataNode, SqlDialect.MySql, null, null);
        SqlPlanManager sqlPlanManager = new SqlPlanManager(logicDbConfig);
        String sql = "update  t_order set status=? where order_id=?";
        sqlPlanManager.getSqlPlan(sql);
        sql = "UPDATE T_ORDER SET STATUS=? WHERE ORDER_ID=?";
        sqlPlanManager.getSqlPlan(sql);

        sql = "UPDATE T_ORDER SET STATUS=? WHERE ORDER_ID=? and status=? ";
        sqlPlanManager.getSqlPlan(sql);
    }


}
