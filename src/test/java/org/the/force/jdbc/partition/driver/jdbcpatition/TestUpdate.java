package org.the.force.jdbc.partition.driver.jdbcpatition;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcPartitionBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.MessageFormat;
import java.time.LocalDate;

/**
 * Created by xuji on 2017/5/30.
 */
@Test(priority = 400)
public class TestUpdate extends TestJdbcPartitionBase {

    /**
     * @throws Exception
     */
    @Test(priority = 401)
    public void testBatch() throws Exception {
        Connection connection = super.getConnection();
        connection.close();
        long start = System.currentTimeMillis();
        connection = getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection
            .prepareStatement("INSERT INTO  t_user(id,channel,app_id,identifier,birth_date,status) VALUES(?,?,?,?,?,?)  ON DUPLICATE KEY UPDATE app_id=app_id,status=status");
        int startIndex = 0;
        int total = startIndex + 2000;
        for (int i = startIndex; i < total; i++) {
            preparedStatement.setInt(1, i + 1);
            preparedStatement.setInt(2, 0);
            preparedStatement.setString(3, "app_" + (i % 2));
            preparedStatement.setString(4, i + "");
            preparedStatement.setDate(5, java.sql.Date.valueOf(LocalDate.of(1950 + i % 50, i % 12 + 1, i % 25 + 1)));
            preparedStatement.setString(6, "batch");
            preparedStatement.addBatch();
            if (i > 0 && i % 200 == 0) {//10
                int[] array = preparedStatement.executeBatch();
                String msg = MessageFormat.format("number={0},size={1}", i, array.length);
                logger.info(msg);
            }
        }
        int[] array = preparedStatement.executeBatch();
        logger.info("result size ="+array.length);
        connection.commit();
        logger.info("耗时=" + (System.currentTimeMillis() - start));
        connection.close();
    }

    @Test(priority = 402)
    public void testInsert1() throws Exception {
        Connection connection = getConnection();
        connection.close();
        connection = getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(
            "INSERT INTO  t_user(id,channel,app_id,identifier,birth_date,status) VALUES(?,?,?,?,?,?) ,(?,?,?,?,?,?) ON DUPLICATE KEY UPDATE app_id=app_id,status=status");
        for (int i = 0; i < 2; i++) {
            preparedStatement.setInt(i * 6 + 1, i + 1);//id
            preparedStatement.setInt(i * 6 + 2, 0);//channel
            preparedStatement.setString(i * 6 + 3, "app_" + (i / 2));
            preparedStatement.setString(i * 6 + 4, "identifier_" + i);
            preparedStatement.setDate(i * 6 + 5, java.sql.Date.valueOf(LocalDate.of(1990, 1, 1)));
            preparedStatement.setString(i * 6 + 6, "ok");
        }
        int result = preparedStatement.executeUpdate();
        logger.info("result="+ result);
        connection.close();
    }

    @Test(priority = 402)
    public void testUpdate1() throws Exception {
        Connection connection = getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE db_order.t_user  SET status=?   WHERE t_user.channel=? AND  t_user.id IN (?,?,?,?)");
        preparedStatement.setString(1, "in-re");
        preparedStatement.setInt(2, 0);
        preparedStatement.setInt(3, 1);
        preparedStatement.setInt(4, 2);
        preparedStatement.setInt(5, 4);
        preparedStatement.setInt(6, 10);
        int result = preparedStatement.executeUpdate();
        logger.info("result=" + result);
        connection.commit();
        connection.close();
    }

    @Test(priority = 402)
    public void testUpdate2() throws Exception {
        Connection connection = getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement("UPDATE db_order.t_user  SET status=?   WHERE t_user.channel=? AND  t_user.id = ?");
        preparedStatement.setString(1, "testUpdate2");
        preparedStatement.setInt(2, 0);
        preparedStatement.setInt(3, 1);
        int result = preparedStatement.executeUpdate();
        logger.info("result=" + result);
        connection.commit();
        connection.close();
    }

    @Test(priority = 402)
    public void testDelete() throws Exception {
        Connection connection = getConnection();
        connection.setAutoCommit(false);
        PreparedStatement preparedStatement = connection.prepareStatement("DELETE  FROM db_order.t_user  WHERE id IN (?,?)");
        preparedStatement.setInt(1, 1);
        preparedStatement.setInt(2, 2);
        int result = preparedStatement.executeUpdate();
        logger.info("result=" + result);
        connection.commit();
        connection.close();
    }

}
