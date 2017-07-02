package org.the.force.jdbc.partition.common.cache;

/**
 * Created by xuji on 2017/6/2.
 */
public interface CachedResource {

    /**
     * 缓存被读取事件
     */
    void getFromCache();
    /**
     * 缓存过期移出事件
     */
    void expireFromCache();

}
