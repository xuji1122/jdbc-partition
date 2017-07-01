package org.the.force.jdbc.partition.driver.jdbc;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcBase;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by xuji on 2017/7/1.
 */
@Test(priority = 20)
public class TestMetaData extends TestJdbcBase {


    public void test1() throws Exception {
        Connection connection = getConnection();
        DatabaseMetaData dsMetaData = connection.getMetaData();
        ResultSet resultSet = dsMetaData.getTables("test", "test", "t_user", new String[] {"TABLE"});
        logger.info("表格信息");
        printResultSet(resultSet);
        resultSet = dsMetaData.getColumns("test", "test", "t_user", null);
        logger.info("column信息");
        printResultSet(resultSet);
        resultSet = dsMetaData.getPrimaryKeys("test", "test", "t_user");
        logger.info("primaryKeys信息");
        printResultSet(resultSet);

        resultSet = dsMetaData.getIndexInfo("test", "test", "t_user", false, false);
        logger.info("index信息");
        printResultSet(resultSet);

        Statement statement = connection.createStatement();
        resultSet = statement.executeQuery("DESC t_user");
        logger.info("desc 信息");
        printResultSet(resultSet);

        connection.close();

    }
}
