package org.the.force.jdbc.partition.driver.jdbcpatition;

import org.the.force.jdbc.partition.TestSupport;

import java.sql.Connection;

/**
 * Created by xuji on 2017/7/1.
 */

public class TestDriverInstance {

    //@Test(priority = 300)
    public void test1() throws Exception {
        int size = 100;
        Thread[] ts = new Thread[size];
        for (int i = 0; i < size; i++) {
            ts[i] = new Thread(() -> {
                try {
                    Connection connection = TestSupport.partitionDb.getConnection();
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
