package org.the.force.jdbc.partition.driver.jdbc;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcSupport;
import org.the.force.jdbc.partition.TestSupport;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by xuji on 2017/7/1.
 */
@Test(priority = 20)
public class TestMetaData {


    public void test1() throws Exception {
        Connection connection = TestSupport.singleDb.getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getTables("test", null, null, new String[] {"TABLE"});
        TestSupport.logger.info("表格信息");
        TestSupport.printResultSet(resultSet);
        resultSet = dsMetaData.getColumns("test", "test", "t_user", null);
        TestSupport.logger.info("column信息");
        TestSupport.printResultSet(resultSet);
        resultSet = dsMetaData.getPrimaryKeys("test", "test", "t_user");
        TestSupport.logger.info("primaryKeys信息");
        TestSupport.printResultSet(resultSet);

        resultSet = dsMetaData.getIndexInfo("test", "test", "t_user", false, false);
        TestSupport.logger.info("index信息");
        TestSupport.printResultSet(resultSet);

        Statement statement = connection.createStatement();
        resultSet = statement.executeQuery("DESC t_user");
        TestSupport.logger.info("desc 信息");
        TestSupport.printResultSet(resultSet);
        connection.close();
    }

    public void test2() throws Exception {
        Connection connection = TestSupport.singleDb.getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getSchemas(null, null);
        TestSupport.logger.info("schemas 信息1");
        TestSupport.printResultSet(resultSet);
        resultSet = dsMetaData.getSchemas();
        TestSupport.logger.info("schemas 信息2");
        TestSupport.printResultSet(resultSet);
        connection.close();
    }

    public void test3() throws Exception {
        Connection connection = TestSupport.singleDb.getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getCatalogs();
        TestSupport.logger.info("catalogs 信息");
        TestSupport.printResultSet(resultSet);
        connection.close();
    }

    public void test4() throws Exception {
        Connection connection = TestSupport.singleDb.getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getTableTypes();
        TestSupport.logger.info("tableTypes 信息");
        TestSupport.printResultSet(resultSet);
        connection.close();
    }

    public void test5() throws Exception {
        Connection connection = TestSupport.singleDb.getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getColumns("test", null, null, null);
        TestSupport.logger.info("全库column信息");
        TestSupport.printResultSet(resultSet);
        connection.close();
    }
}
