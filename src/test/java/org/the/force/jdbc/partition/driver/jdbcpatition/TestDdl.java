package org.the.force.jdbc.partition.driver.jdbcpatition;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcPartitionBase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * Created by xuji on 2017/6/19.
 */
@Test(priority = 100)
public class TestDdl extends TestJdbcPartitionBase {


    public void testDropCreateTable() throws Exception {
        Connection connection = super.getConnection();
        connection.close();
        connection = DriverManager.getConnection(dbConnectionUrl, user, password);
        String[] tableNames = new String[] {"user/t_user", "order/t_order", "order/t_order_sku"};
        for (int i = 0; i < tableNames.length; i++) {
            String tableName = tableNames[i].substring(tableNames[i].lastIndexOf('/') + 1);
            String path = tableNames[i] + ".sql";
            String[] sqls = super.loadSqlFromFile(path);
            PreparedStatement preparedStatement = connection.prepareStatement(sqls[0]);
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            logger.info("drop table {} result={}", tableName, result);
            preparedStatement = connection.prepareStatement(sqls[1]);
            result = preparedStatement.executeUpdate();
            logger.info("create table {} result={}", tableName, result);
            preparedStatement.close();
        }
        connection.close();
    }
}
