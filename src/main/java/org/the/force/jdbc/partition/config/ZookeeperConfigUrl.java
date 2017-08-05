package org.the.force.jdbc.partition.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.the.force.jdbc.partition.exception.PartitionConfigException;

import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by xuji on 2017/7/29.
 */
public class ZookeeperConfigUrl implements ConfigUrl {

    private final String url;

    public ZookeeperConfigUrl(String url) {
        int index = url.indexOf("://");
        this.url = url.substring(index + 3);
    }

    public DataNode getLogicDbConfigNode(Properties info) throws SQLException {
        int pathIndex = url.indexOf("/");
        if (pathIndex < 0) {
            throw new PartitionConfigException("pathIndex < 0");
        }
        String connectStr = url.substring(0, pathIndex);
        String logicDbPath = url.substring(pathIndex + 1);
        if (logicDbPath.endsWith("/")) {
            logicDbPath = logicDbPath.substring(0, logicDbPath.length() - 1);
        }
        if (logicDbPath.startsWith("/")) {
            logicDbPath = logicDbPath.substring(1);
        }
        int index = logicDbPath.lastIndexOf('/');
        String namespace = null;
        if (index > -1) {
            namespace = logicDbPath.substring(0, index);
        }
        String logicDbName = logicDbPath.substring(index + 1).toLowerCase();
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(500, 3);
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().connectString(connectStr);
        if (namespace != null) {
            builder.namespace(namespace);
        }
        builder.retryPolicy(retryPolicy);
        builder.connectionTimeoutMs(Integer.parseInt(info.getProperty("zk.connectionTimeoutMs", "15000")));
        builder.sessionTimeoutMs(Integer.parseInt(info.getProperty("zk.sessionTimeoutMs", "20000")));
        CuratorFramework curatorFramework = builder.build();
        curatorFramework.start();
        return new ZookeeperDataNode(null, logicDbName, curatorFramework);
    }

    public String toString() {
        return "zookeeper://" + url;
    }
}
