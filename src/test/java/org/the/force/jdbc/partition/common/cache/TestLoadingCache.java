package org.the.force.jdbc.partition.common.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestSupport;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.executor.SqlExecutorManager;
import org.the.force.jdbc.partition.resource.executor.SqlKey;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

/**
 * Created by xuji on 2017/6/2.
 */
public class TestLoadingCache {

    private static Log logger = LogFactory.getLog(TestLoadingCache.class);

    @Test
    public void test1() throws Exception {
        LoadingCache<String, String> loadingCache =
            CacheBuilder.newBuilder().maximumSize(1024).concurrencyLevel(1024).initialCapacity(512).build(new CacheLoader<String, String>() {
                public String load(String key) throws Exception {
                    logger.info("load " + key);
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

        LogicDbConfig logicDbConfig = TestSupport.partitionDb.ymlLogicDbConfig;
        SqlExecutorManager sqlExecutorManager = new SqlExecutorManager(logicDbConfig);
        String sql = "update  t_order set status=? where order_id=?";
        sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        sql = "UPDATE T_ORDER SET STATUS=? WHERE ORDER_ID=?";
        sqlExecutorManager.getSqlExecutor(new SqlKey(sql));

        sql = "UPDATE T_ORDER SET STATUS=? WHERE ORDER_ID=? and status=? ";
        sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
    }


}
