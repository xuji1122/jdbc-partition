package org.the.force.jdbc.partition;

import org.the.force.jdbc.partition.common.PartitionSqlUtils;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;
import org.the.force.thirdparty.druid.util.JdbcUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.MessageFormat;

/**
 * Created by xuji on 2017/7/21.
 * 测试优先级 配置初始化100  建表ddl 200  纯测试解析类 300  dml 400  查询 500
 */
public final class TestSupport {

    public static Log logger = LogFactory.getLog(TestSupport.class);

    public static String projectBasePath;

    public static String sqlDialectName;

    public static SqlDialect sqlDialect;

    public static String test_cases_basic_path;

    public static final TestJdbcSupport singleDb;

    public static final TestJdbcPartitionSupport partitionDb;

    static {
        sqlDialectName = System.getProperty("db.dialect", "mysql");
        sqlDialect = SqlDialect.getByName(sqlDialectName);
        projectBasePath = System.getProperty("project.base.path", System.getProperty("user.dir"));
        test_cases_basic_path = projectBasePath + "/test_cases_basic";
        logger.info("sqlDialectName=" + sqlDialectName);
        logger.info("projectBasePath=" + projectBasePath);
        singleDb = new TestJdbcSupport();
        partitionDb = new TestJdbcPartitionSupport();

    }

    /**
     * 加载sql文件，返回sql数组
     *
     * @param filePath
     * @return
     */
    public static String[] loadSqlFromFile(String filePath) {
        filePath = toFullPath(filePath);
        return PartitionSqlUtils.loadSqlFromFile(filePath, sqlDialect);
    }

    public static File getYamlFromFile(String filePath) {
        filePath = toFullPath(filePath);
        return new File(filePath);
    }

    public static String toFullPath(String filePath) {
        if (filePath.startsWith("/")) {
            return test_cases_basic_path + filePath;
        } else {
            return test_cases_basic_path + "/" + filePath;
        }
    }

    public static PrintWriter getPrintWriter(String filePath) throws IOException {
        filePath = toFullPath(filePath);
        OutputStreamWriter ow = new OutputStreamWriter(new FileOutputStream(new File(filePath)), "UTF-8");
        return new PrintWriter(ow);
    }

    public static void closeFile(PrintWriter pw) {
        pw.close();
    }

    public static void printResultSet(ResultSet rs) throws Exception {
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
