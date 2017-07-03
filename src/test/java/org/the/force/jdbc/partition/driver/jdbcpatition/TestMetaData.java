package org.the.force.jdbc.partition.driver.jdbcpatition;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcBase;
import org.the.force.jdbc.partition.TestJdbcPartitionBase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by xuji on 2017/7/1.
 */
@Test(priority = 400)
public class TestMetaData extends TestJdbcPartitionBase {


    public void test1() throws Exception {
        Connection connection = getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getTables("db_order", null, null, new String[] {"TABLE"});
        logger.info("partition 表格信息");
        printResultSet(resultSet);
        resultSet = dsMetaData.getColumns("db_order", "db_order", "t_user", null);
        logger.info("partition column信息");
        printResultSet(resultSet);
        resultSet = dsMetaData.getPrimaryKeys("db_order", "db_order", "t_user");
        logger.info("partition primaryKeys信息");
        printResultSet(resultSet);

        resultSet = dsMetaData.getIndexInfo("db_order", "db_order", "t_user", false, false);
        logger.info("partition index信息");
        printResultSet(resultSet);
        connection.close();
    }

    public void test2() throws Exception {
        Connection connection = getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getSchemas(null, null);
        logger.info("partition schemas 信息1");
        printResultSet(resultSet);
        resultSet = dsMetaData.getSchemas();
        logger.info("partition schemas 信息2");
        printResultSet(resultSet);
        connection.close();
    }

    public void testCatalogs() throws Exception {
        Connection connection = getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getCatalogs();
        logger.info("partition catalogs 信息");
        printResultSet(resultSet);
        connection.close();
    }

    public void test4() throws Exception {
        Connection connection = getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getTableTypes();
        logger.info("partition tableTypes 信息");
        printResultSet(resultSet);
        connection.close();
    }

    public void test5() throws Exception {
        Connection connection = getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getColumns("db_order", null, null, null);
        logger.info("partition 全库column信息");
        printResultSet(resultSet);
        connection.close();
    }

//    public void test6() throws Exception {
//        Connection connection = getConnection();
//        Statement statement = connection.createStatement();
//        ResultSet resultSet = statement.executeQuery("DESC t_user");
//        logger.info("partition desc 信息");
//        printResultSet(resultSet);
//    }
}
