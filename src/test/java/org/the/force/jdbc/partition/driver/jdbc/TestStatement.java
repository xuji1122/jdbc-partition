package org.the.force.jdbc.partition.driver.jdbc;

import org.the.force.jdbc.partition.TestSupport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.MessageFormat;
import java.time.LocalDate;

/**
 * Created by xuji on 2017/5/31.
 * 1，jdbcPartitionConnection 缓存engine prepared statement  关闭时清空  预编译的作用
 * 2，executeBatch 无论是否异常清空batch 只是清空参数列表 编译好的对象不清空
 * 3，clearParameters  手动调用才会清空 异常之后可以重复  不去清空
 * 4，statement是否需要缓存的问题 不缓存 很少使用 无法预见sql 预编译也不存在
 */
//@Test(priority = 20)
public class TestStatement  {

    private String testSql = "INSERT INTO  t_user(id,channel,app_id,identifier,birth_date,status) VALUES(?,?,?,?,?,?)  ON DUPLICATE KEY UPDATE app_id=app_id,status=status";

    public void testBatch() throws Exception {
        long start = System.currentTimeMillis();
        Connection connection = TestSupport.singleDb.getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement(testSql);
        int startIndex = 0;
        int total = startIndex + 2000;
        for (int i = startIndex; i < total; i++) {
            preparedStatement.setInt(1, i);
            preparedStatement.setInt(2, 0);
            preparedStatement.setString(3, "app_" + (i % 2));
            preparedStatement.setString(4, i + "");
            preparedStatement.setDate(5, java.sql.Date.valueOf(LocalDate.of(1950 + i % 50, i % 12 + 1, i % 25 + 1)));
            preparedStatement.setString(6, "batch");
            preparedStatement.addBatch();
            if (i > 0 && i % 200 == 0) {//10
                int[] array = preparedStatement.executeBatch();
                TestSupport.logger.info(MessageFormat.format("number={0},size={1}", i, array.length));
            }
        }
        int[] array = preparedStatement.executeBatch();
        TestSupport.logger.info("size="+ array.length);
        connection.commit();
        TestSupport.logger.info("耗时=" + (System.currentTimeMillis() - start));
        connection.close();
    }


}
