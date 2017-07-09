package org.the.force.jdbc.partition;

import org.the.force.jdbc.partition.common.BeanUtils;
import org.the.force.jdbc.partition.driver.JdbcPartitionDriver;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.rule.config.JsonDataNode;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;

/**
 * Created by xuji on 2017/7/1.
 * 测试优先级 建表100  配置初始化200  纯测试解析类 300  update 400 查询 500
 */
public class TestJdbcPartitionBase extends TestJdbcBase {

    protected String zkRootPath;
    protected String zkConnectStr;
    protected String defaultPhysicDbConnectionUrlPrefix;
    //数据库名和表名约定不变
    protected final String logicDbName = "db_order";

    protected String paramStr;

    protected Properties propInfo;

    protected JsonDataNode jsonDbDataNode;

    public TestJdbcPartitionBase() {
        super();
    }

    protected void init() {
        sqlDialectName = System.getProperty("sql.dialect", "mysql");
        sqlDialect = SqlDialect.getByName(sqlDialectName);
        defaultPhysicDbHost = System.getProperty("defaultPhysicDbHost", "localhost:3306");
        user = System.getProperty("db.user", "root");
        password = System.getProperty("db.user.password", "123456");
        projectBasePath = System.getProperty("project.base.path", System.getProperty("user.dir"));
        logger.info("sqlDialectName=" + sqlDialectName);
        logger.info("defaultPhysicDbHost=" + defaultPhysicDbHost);
        logger.info(MessageFormat.format("user={0},password={1}", user, password));
        logger.info("projectBasePath=" + projectBasePath);

        zkConnectStr = System.getProperty("zk.connect.str", "localhost:2181");
        zkRootPath = "db/" + sqlDialectName + "db";
        logger.info("zkConnectStr=" + zkConnectStr);
        logger.info("zkRootPath=" + zkRootPath);
        //TODO 可能变化的点
        actualDriverClassName = "com.mysql.jdbc.Driver";
        dbConnectionUrl = "jdbc:partition:" + sqlDialectName + "@" + zkConnectStr + "/" + zkRootPath + "/" + logicDbName
            + "?characterEncoding=utf-8&allowMultiQueries=true&cachePrepStmts=true&useServerPrepStmts=false";
        defaultPhysicDbConnectionUrlPrefix = "jdbc:mysql://" + defaultPhysicDbHost + "/";
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
        logger.info("defaultPhysicDbConnectionUrlPrefix={}" + defaultPhysicDbConnectionUrlPrefix);

        Yaml yml = new Yaml();
        Object object;
        try {
            object = yml.load(new FileInputStream(getYamlFromFile(logicDbName + ".yml")));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        jsonDbDataNode = new JsonDataNode(null, logicDbName, (Map<String, Object>) ((Map<String, Object>) object).get(logicDbName));
        logger.info("yml config info:\n" + object.toString());
        logger.info("implClass:" + object.getClass().getName());
        String json = BeanUtils.toJson(jsonDbDataNode);
        logger.info("json:\n" + json);
    }
}
