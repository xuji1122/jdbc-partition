package org.the.force.jdbc.partition.common;

/**
 * Created by xuji on 2017/7/15.
 */
public class IgnoreCaseKey {

    private final String key;

    public IgnoreCaseKey(String key) {
        this(null, key);
    }

    public IgnoreCaseKey(String tableName, String key) {
        if (tableName == null || tableName.length() < 1) {
            this.key = key.toLowerCase();
        } else {
            this.key = (tableName + "." + key).toLowerCase();
        }
    }
    public String getKey() {
        return key;
    }

}
