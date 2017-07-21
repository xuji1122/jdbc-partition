package org.the.force.jdbc.partition;

import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.MessageFormat;

/**
 * Created by xuji on 2017/7/1.
 */
public final class TestJdbcSupport {

    public static Log logger = LogFactory.getLog(TestSupport.class);

    public String defaultPhysicDbHost;
    public String user;
    public String password;
    public String dbConnectionUrl;
    public String actualDriverClassName;
    //数据库名和表名约定不变
    public final String dbName = "test";


    public TestJdbcSupport() {
        init();
    }

    protected void init() {
        defaultPhysicDbHost = System.getProperty("defaultPhysicDbHost", "localhost:3306");
        user = System.getProperty("db.user", "root");
        password = System.getProperty("db.user.password", "123456");
        logger.info("defaultPhysicDbHost=" + defaultPhysicDbHost);
        logger.info(MessageFormat.format("user={0},password={1}", user, password));
        //TODO 可能变化的点
        actualDriverClassName = "com.mysql.jdbc.Driver";
        dbConnectionUrl = "jdbc:mysql://" + defaultPhysicDbHost + "/" + dbName + "?characterEncoding=utf-8&allowMultiQueries=true&cachePrepStmts=true&useServerPrepStmts=false";
        logger.info("actualDriverClassName=" + actualDriverClassName);
        logger.info("dbConnectionUrl=" + dbConnectionUrl);
        try {
            Class.forName(actualDriverClassName);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public  Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbConnectionUrl, user, password);
    }



}
