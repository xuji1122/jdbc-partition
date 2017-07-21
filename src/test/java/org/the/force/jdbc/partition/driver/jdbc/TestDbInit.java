package org.the.force.jdbc.partition.driver.jdbc;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestSupport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * Created by xuji on 2017/7/1.
 */
@Test(priority = 1)
public class TestDbInit {

    public void testDropCreateTable() throws Exception {
        Connection connection = TestSupport.singleDb.getConnection();
        connection.close();
        connection = TestSupport.singleDb.getConnection();
        String[] tableNames = new String[] {"user/t_user", "order/t_order", "order/t_order_sku", "product/t_spu", "test/test"};
        Statement statement = connection.createStatement();
        for (int i = 0; i < tableNames.length; i++) {
            String path = tableNames[i] + ".ddl.sql";
            String[] sqls = TestSupport.loadSqlFromFile(TestSupport.test_cases_basic_schema_path + "/" + path);
            for (int k = 0; k < sqls.length; k++) {
                statement.execute(sqls[k]);
            }
        }
        connection.close();
    }
}
