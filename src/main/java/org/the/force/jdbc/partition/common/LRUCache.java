package org.the.force.jdbc.partition.common;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by xuji on 2017/6/2.
 */
public class LRUCache<K, V extends CachedResource> extends LinkedHashMap<K, V> {


    private static final long serialVersionUID = -308769723071305284L;

    private final int maxSize;

    public LRUCache(int maxSize) {
        super(maxSize, 0.75f, true);
        this.maxSize = maxSize;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        boolean remove = (size() > maxSize);
        if (remove) {
            V cachedResource = eldest.getValue();
            cachedResource.expireFromCache();
        }
        return remove;
    }

    public V get(Object key) {
        V v = super.get(key);
        if (v != null) {
            v.getFromCache();
        }
        return v;
    }



}
