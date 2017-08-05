package org.the.force.jdbc.partition.driver;

import org.the.force.jdbc.partition.common.BeanUtils;
import org.the.force.jdbc.partition.config.ConfigUrl;
import org.the.force.jdbc.partition.resource.db.LogicDbManager;
import org.the.force.jdbc.partition.resource.executor.SqlExecutorManager;
import org.the.force.jdbc.partition.config.DataNode;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xuji on 2017/7/1.
 * 一个LogicDb的配置的实例对应一个DbDriverInstance
 */
public class LogicDbDriverInstance {

    private static Log logger = LogFactory.getLog(LogicDbDriverInstance.class);

    private final JdbcPartitionUrl jdbcPartitionUrl;

    private final Properties info;

    private final AtomicLong threadId = new AtomicLong(0);

    /**
     * sql执行器缓存
     */
    private SqlExecutorManager sqlExecutorManager;
    /**
     * 如果一个逻辑sql的分区结果是需要在n个物理db上执行，那么前面n-1将会交给sqlExecutorPool异步执行，最后一个由当前线程执行并等待前面n-1个结果返回
     */
    private ThreadPoolExecutor sqlExecutorPool;

    private DataNode rootDataNode;

    private LogicDbManager logicDbManager;


    public LogicDbDriverInstance(JdbcPartitionUrl jdbcPartitionUrl, Properties info) throws SQLException {
        this.jdbcPartitionUrl = jdbcPartitionUrl;
        this.info = info;
        init();
    }

    public void init() throws SQLException {
        logger.info("初始化db驱动开始:" + jdbcPartitionUrl.toString());
        JdbcPartitionUrl jdbcPartitionUrl = this.jdbcPartitionUrl;
        ConfigUrl configUrl = jdbcPartitionUrl.getConfigUrl();
        rootDataNode = configUrl.getLogicDbConfigNode(info);//保留对象的引用，防止配置对象被回收
        logicDbManager = new LogicDbManager(rootDataNode, jdbcPartitionUrl.getSqlDialect(), jdbcPartitionUrl.getParamStr(), info);
        sqlExecutorManager = new SqlExecutorManager(logicDbManager);
        sqlExecutorPool =
            new ThreadPoolExecutor(Integer.parseInt(info.getProperty("executor.pool.coreSize", "32")), Integer.parseInt(info.getProperty("executor.pool.maxSize", "256")), 60,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(Integer.parseInt(info.getProperty("executor.pool.queueCapacity", "2560"))));
        sqlExecutorPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        sqlExecutorPool.setThreadFactory(r -> {
            Thread t = new Thread(r);
            t.setName(logicDbManager.getLogicDbName() + "-executor-" + threadId.addAndGet(1));
            return t;
        });
        try {
            logicDbManager.loadDbMetaData();
        } catch (Exception e) {
            logger.warn("初始化DbMetaData异常", e);
        }
        String configJson = BeanUtils.toJson(this.logicDbManager);
        logger.info(MessageFormat.format("获取到db配置信息:{0}", configJson));
    }

    public Connection newConnection() {
        return new JdbcPartitionConnection(logicDbManager, sqlExecutorManager, sqlExecutorPool);
    }
}
