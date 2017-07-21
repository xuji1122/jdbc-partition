package org.the.force.jdbc.partition;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.the.force.jdbc.partition.common.BeanUtils;
import org.the.force.jdbc.partition.driver.JdbcPartitionDriver;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.resource.db.LogicDbManager;
import org.the.force.jdbc.partition.rule.config.JsonDataNode;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;

/**
 * Created by xuji on 2017/7/1.
 */
public final class TestJdbcPartitionSupport {

    public static Log logger = LogFactory.getLog(TestSupport.class);

    public String user;
    public String password;
    public String dbConnectionUrl;
    public String actualDriverClassName;
    public String zkRootPath;
    public String zkConnectStr;
    //数据库名和表名约定不变
    public final String logicDbName = "db_order";

    public String paramStr;

    public Properties propInfo;

    public JsonDataNode jsonDbDataNode;

    public LogicDbManager ymlLogicDbConfig;

    private volatile CuratorFramework curatorFramework;


    public TestJdbcPartitionSupport() {
        super();
        init();
    }

    protected void init() {
        user = System.getProperty("db.user", "root");
        password = System.getProperty("db.user.password", "123456");
        logger.info(MessageFormat.format("user={0},password={1}", user, password));

        zkConnectStr = System.getProperty("zk.connect.str", "localhost:2181");
        zkRootPath = "db/" + TestSupport.sqlDialectName + "db";
        logger.info("zkConnectStr=" + zkConnectStr);
        logger.info("zkRootPath=" + zkRootPath);
        //TODO 可能变化的点
        actualDriverClassName = "com.mysql.jdbc.Driver";
        dbConnectionUrl = "jdbc:partition:" + TestSupport.sqlDialectName + "@" + zkConnectStr + "/" + zkRootPath + "/" + logicDbName
            + "?characterEncoding=utf-8&allowMultiQueries=true&cachePrepStmts=true&useServerPrepStmts=false";
        try {
            Class<?> clazz = JdbcPartitionDriver.class;
            Class.forName(clazz.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        paramStr = "characterEncoding=utf-8&allowMultiQueries=true&cachePrepStmts=true&useServerPrepStmts=false";
        propInfo = new Properties();
        propInfo.put("user", user);
        propInfo.put("password", password);
        logger.info("actualDriverClassName=" + actualDriverClassName);
        logger.info("dbConnectionUrl=" + dbConnectionUrl);

        Yaml yml = new Yaml();
        Object object;
        try {
            object = yml.load(new FileInputStream(TestSupport.getYamlFromFile(TestSupport.test_cases_basic_path + "/schema/" + logicDbName + ".yml")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        jsonDbDataNode = new JsonDataNode(null, logicDbName, (Map<String, Object>) ((Map<String, Object>) object).get(logicDbName));
        try {
            ymlLogicDbConfig = new LogicDbManager(jsonDbDataNode, SqlDialect.MySql, paramStr, propInfo);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        logger.info("yml config info:\n" + object.toString());
        logger.info("implClass:" + object.getClass().getName());
        String json = BeanUtils.toJson(jsonDbDataNode);
        logger.info("yml config json:\n" + json);
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbConnectionUrl, user, password);
    }

    public CuratorFramework getZk() {
        if (curatorFramework == null) {
            synchronized (this) {
                if (curatorFramework == null) {
                    ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(500, 3);
                    CuratorFramework curatorFramework =
                        CuratorFrameworkFactory.builder().connectString(zkConnectStr).namespace("db/" + TestSupport.sqlDialectName + "db").connectionTimeoutMs(15000)
                            .sessionTimeoutMs(20000).retryPolicy(retryPolicy).build();
                    this.curatorFramework = curatorFramework;
                    this.curatorFramework.start();
                }
            }
        }
        return curatorFramework;
    }

}
