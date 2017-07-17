package org.the.force.jdbc.partition;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;
import org.the.force.thirdparty.druid.util.JdbcUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.MessageFormat;

/**
 * Created by xuji on 2017/7/1.
 */
public class TestJdbcBase {

    protected Log logger = LogFactory.getLog(this.getClass());
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
        sqlDialectName = System.getProperty("executor.dialect", "mysql");
        sqlDialect = SqlDialect.getByName(sqlDialectName);
        defaultPhysicDbHost = System.getProperty("defaultPhysicDbHost", "localhost:3306");
        user = System.getProperty("db.user", "root");
        password = System.getProperty("db.user.password", "123456");
        projectBasePath = System.getProperty("project.base.path", System.getProperty("user.dir"));
        logger.info("sqlDialectName=" + sqlDialectName);
        logger.info("defaultPhysicDbHost=" + defaultPhysicDbHost);
        logger.info(MessageFormat.format("user={0},password={1}", user, password));
        logger.info("projectBasePath=" + projectBasePath);
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

    protected File getYamlFromFile(String filePath) {
        filePath = projectBasePath + "/doc/" + sqlDialectName + "/" + filePath;
        return new File(filePath);
    }

    protected Connection getConnection() throws SQLException {
        return DriverManager.getConnection(dbConnectionUrl, user, password);
    }

    protected void printResultSet(ResultSet rs) throws Exception {
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        logger.info("columnCount:" + columnCount);
        for (int i = 1; i <= columnCount; i++) {
            System.out.print(i + ":\n\r");
            System.out.println((MessageFormat.format("columnName:{0},columnLabel:{1},sqlType:jdbcType={2},jdbcTypeName={3},columnTypeName={4}", resultSetMetaData.getColumnName(i),
                resultSetMetaData.getColumnLabel(i), resultSetMetaData.getColumnType(i), JdbcUtils.getTypeName(resultSetMetaData.getColumnType(i)),
                resultSetMetaData.getColumnTypeName(i))));
            System.out.println(MessageFormat
                .format("tableName={0},shameName={1},catalogName={2}", resultSetMetaData.getTableName(i), resultSetMetaData.getSchemaName(i), resultSetMetaData.getCatalogName(i)));
        }
        JdbcUtils.printResultSet(rs);
    }

}
