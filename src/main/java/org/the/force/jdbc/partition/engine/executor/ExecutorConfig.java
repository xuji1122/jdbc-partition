package org.the.force.jdbc.partition.engine.executor;

/**
 * Created by xuji on 2017/6/1.
 */
public class ExecutorConfig {


    private volatile int fetchSize;

    public ExecutorConfig() {
    }

    public int getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }
}
