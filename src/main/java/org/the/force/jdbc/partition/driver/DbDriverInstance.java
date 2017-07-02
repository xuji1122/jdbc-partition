package org.the.force.jdbc.partition.driver;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.the.force.jdbc.partition.common.BeanUtils;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.db.LogicDbManager;
import org.the.force.jdbc.partition.resource.sql.SqlPlanManager;
import org.the.force.jdbc.partition.rule.config.DataNode;
import org.the.force.jdbc.partition.rule.config.ZKDataNode;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xuji on 2017/7/1.
 */
public class DbDriverInstance {

    private static Logger logger = LoggerFactory.getLogger(DbDriverInstance.class);

    private final JdbcPartitionUrl jdbcPartitionUrl;

    private final Properties info;

    private final AtomicLong threadId = new AtomicLong(0);

    /**
     * 数据库配置对象
     */
    private volatile LogicDbConfig logicDdConfig;

    /**
     * sql执行计划缓存
     */
    private SqlPlanManager sqlPlanManager;
    /**
     * 如果一个逻辑sql的分区结果是需要在n个物理db上执行，那么前面n-1将会交给sqlExecutorPool异步执行，最后一个由当前线程执行并等待前面n-1个结果返回
     */
    private ThreadPoolExecutor sqlExecutorPool;

    /**
     * zk client端
     */
    private CuratorFramework curatorFramework;


    public DbDriverInstance(JdbcPartitionUrl jdbcPartitionUrl, Properties info) throws SQLException {
        this.jdbcPartitionUrl = jdbcPartitionUrl;
        this.info = info;
        init();
    }

    public void init() throws SQLException {
        JdbcPartitionUrl jdbcPartitionUrl = this.jdbcPartitionUrl;
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(500, 3);
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder().connectString(jdbcPartitionUrl.getConnectString());
        if (jdbcPartitionUrl.getNamespace() != null) {
            builder.namespace(jdbcPartitionUrl.getNamespace());
        }
        builder.retryPolicy(retryPolicy);
        builder.connectionTimeoutMs(Integer.parseInt(info.getProperty("zk.connectionTimeoutMs", "15000")));
        builder.sessionTimeoutMs(Integer.parseInt(info.getProperty("zk.sessionTimeoutMs", "20000")));
        curatorFramework = builder.build();
        curatorFramework.start();
        DataNode zkDataNode = new ZKDataNode(null, jdbcPartitionUrl.getLogicDbName(), curatorFramework);
        LogicDbManager logicDbManager = new LogicDbManager(zkDataNode, jdbcPartitionUrl.getSqlDialect(), jdbcPartitionUrl.getParamStr(), info);
        sqlPlanManager = new SqlPlanManager(logicDbManager);
        sqlExecutorPool =
            new ThreadPoolExecutor(Integer.parseInt(info.getProperty("sql.executor.pool.coreSize", "32")), Integer.parseInt(info.getProperty("sql.executor.pool.maxSize", "256")),
                60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(Integer.parseInt(info.getProperty("sql.executor.pool.queueCapacity", "2560"))));
        sqlExecutorPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        sqlExecutorPool.setThreadFactory(r -> {
            Thread t = new Thread(r);
            t.setName(jdbcPartitionUrl.getLogicDbName() + "-executor-" + threadId.addAndGet(1));
            return t;
        });
        this.logicDdConfig = logicDbManager;
        logger.info("获取到db配置信息:{}", BeanUtils.toJson(this.logicDdConfig));
    }
    public Connection newConnection() {
        return new JdbcPartitionConnection(logicDdConfig, sqlPlanManager, sqlExecutorPool);
    }
}
