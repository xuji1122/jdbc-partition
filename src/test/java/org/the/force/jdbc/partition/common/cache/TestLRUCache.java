package org.the.force.jdbc.partition.common.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;


/**
 * Created by xuji on 2017/6/2.
 */
public class TestLRUCache {

    private static Logger logger = LoggerFactory.getLogger(TestLRUCache.class);

    @Test
    public void test1() {
        LRUCache cache = new LRUCache<String, MyResoure>(4);
        cache.put("1", new MyResoure("1"));
        cache.put("2", new MyResoure("2"));
        logger.info("map:{}",cache.keySet());
        cache.put("1", new MyResoure("1"));
        cache.put("3", new MyResoure("4"));
        cache.put("1", new MyResoure("1"));
        logger.info("map:{}",cache.keySet());

    }

    public static class MyResoure implements CachedResource {
        private String key;

        public MyResoure(String key) {
            this.key = key;
        }

        @Override
        public void expireFromCache() {
            logger.info("expireFromCache {}", key);
        }

        @Override
        public void getFromCache() {

        }
    }

}
