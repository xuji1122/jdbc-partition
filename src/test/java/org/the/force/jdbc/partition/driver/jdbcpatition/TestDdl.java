package org.the.force.jdbc.partition.driver.jdbcpatition;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestSupport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.MessageFormat;

/**
 * Created by xuji on 2017/6/19.
 */
@Test(priority = 200)
public class TestDdl {

    public void testDropCreateTable() throws Exception {
        Connection connection = TestSupport.partitionDb.getConnection();
        connection.close();
        connection = TestSupport.partitionDb.getConnection();
        String[] tableNames = new String[] {"schema/" + TestSupport.sqlDialectName + "/user/t_user", "schema/" + TestSupport.sqlDialectName + "/order/t_order",
            "schema/" + TestSupport.sqlDialectName + "/order/t_order_sku"};
        for (int i = 0; i < tableNames.length; i++) {
            String tableName = tableNames[i].substring(tableNames[i].lastIndexOf('/') + 1);
            String path = tableNames[i] + ".ddl.sql";
            String[] sqls = TestSupport.loadSqlFromFile(path);
            PreparedStatement preparedStatement = connection.prepareStatement(sqls[0]);
            int result = preparedStatement.executeUpdate();
            preparedStatement.close();
            TestSupport.logger.info(MessageFormat.format("drop table {0} result={1}", tableName, result));
            preparedStatement = connection.prepareStatement(sqls[1]);
            result = preparedStatement.executeUpdate();
            TestSupport.logger.info(MessageFormat.format("create table {0} result={1}", tableName, result));
            preparedStatement.close();
        }
        connection.close();
    }
}
