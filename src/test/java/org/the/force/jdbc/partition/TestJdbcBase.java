package org.the.force.jdbc.partition;

import org.the.force.thirdparty.druid.util.JdbcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.driver.SqlDialect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by xuji on 2017/7/1.
 */
public class TestJdbcBase {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    protected String sqlDialectName;
    protected String defaultPhysicDbHost;
    protected String user;
    protected String password;
    protected String projectBasePath;
    protected SqlDialect sqlDialect;
    protected String dbConnectionUrl;
    protected String actualDriverClassName;
    //数据库名和表名约定不变
    protected final String dbName = "test";

    public TestJdbcBase() {
        init();
    }

    protected void init() {
        sqlDialectName = System.getProperty("sql.dialect", "mysql");
        sqlDialect = SqlDialect.getByName(sqlDialectName);
        defaultPhysicDbHost = System.getProperty("defaultPhysicDbHost", "localhost:3306");
        user = System.getProperty("db.user", "root");
        password = System.getProperty("db.user.password", "123456");
        projectBasePath = System.getProperty("project.base.path", System.getProperty("user.dir"));
        logger.info("sqlDialectName={}", sqlDialectName);
        logger.info("defaultPhysicDbHost={}", defaultPhysicDbHost);
        logger.info("user={},password={}", user, password);
        logger.info("projectBasePath={}", projectBasePath);
        //TODO 可能变化的点
        actualDriverClassName = "com.mysql.jdbc.Driver";
        dbConnectionUrl = "jdbc:mysql://" + defaultPhysicDbHost + "/" + dbName + "?characterEncoding=utf-8&allowMultiQueries=true&cachePrepStmts=true&useServerPrepStmts=false";
        logger.info("actualDriverClassName={}", actualDriverClassName);
        logger.info("dbConnectionUrl={}", dbConnectionUrl);
        try {
            Class.forName(actualDriverClassName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 加载sql文件，返回sql数组
     *
     * @param filePath
     * @return
     */
    protected String[] loadSqlFromFile(String filePath) {
        filePath = projectBasePath + "/doc/" + sqlDialectName + "/" + filePath;
        return PartitionSqlUtils.loadSqlFromFile(filePath, sqlDialect);
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbConnectionUrl, user, password);
    }

    protected void printResultSet(ResultSet rs) throws Exception {
        JdbcUtils.printResultSet(rs);
    }

}
