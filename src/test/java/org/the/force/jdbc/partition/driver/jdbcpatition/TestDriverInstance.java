package org.the.force.jdbc.partition.driver.jdbcpatition;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcPartitionBase;

import java.sql.Connection;

/**
 * Created by xuji on 2017/7/1.
 */
@Test(priority = 300)
public class TestDriverInstance extends TestJdbcPartitionBase {


    public void test1() throws Exception {
        int size = 100;
        Thread[] ts = new Thread[size];
        for (int i = 0; i < size; i++) {
            ts[i] = new Thread(() -> {
                try {
                    Connection connection = getConnection();
                    connection.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            ts[i].start();
        }
        for (Thread t : ts) {
            t.join();
        }
    }
}
