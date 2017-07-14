package org.the.force.jdbc.partition.common.cache;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.engine.executor.query.subqueryexpr.ExitsSubQueriedExpr;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.text.MessageFormat;


/**
 * Created by xuji on 2017/6/2.
 */
public class TestLRUCache {

    private static Log logger = LogFactory.getLog(TestLRUCache.class);

    @Test
    public void test1() throws Exception {
        LRUCache cache = new LRUCache<String, MyResoure>(4);
        cache.put("1", new MyResoure("1"));
        cache.put("2", new MyResoure("2"));
        logger.info(MessageFormat.format("map:{0}", cache.keySet()));
        cache.put("1", new MyResoure("1"));
        cache.put("3", new MyResoure("4"));
        cache.put("1", new MyResoure("1"));
        logger.info(MessageFormat.format("map:{0}", cache.keySet()));
        ExitsSubQueriedExpr exitsSubQueriedExpr = new ExitsSubQueriedExpr(null, null, null, false);
        logger.info("" + exitsSubQueriedExpr.hashCode());

    }

    public static class MyResoure implements CachedResource {
        private String key;

        public MyResoure(String key) {
            this.key = key;
        }

        @Override
        public void expireFromCache() {
            logger.info(MessageFormat.format("expireFromCache {0}", key));
        }

        @Override
        public void getFromCache() {

        }
    }

}
