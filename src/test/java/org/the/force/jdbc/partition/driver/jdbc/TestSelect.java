package org.the.force.jdbc.partition.driver.jdbc;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcBase;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.MessageFormat;

/**
 * Created by xuji on 2017/7/14.
 */
//@Test(priority = 20)
public class TestSelect extends TestJdbcBase {
    private Log log = LogFactory.getLog(TestSelect.class);

    public void testSqlRef() throws Exception {
        Connection connection = getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT t.* FROM (SELECT id,app_id FROM t_user) t");
        ResultSet resultSet = pstmt.executeQuery();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int count = resultSetMetaData.getColumnCount();
        log.info("count=" + count);
        for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
            log.info(MessageFormat.format("tableName={0},columnName={1}", resultSetMetaData.getTableName(i + 1), resultSetMetaData.getColumnName(i + 1)));
        }
        connection.close();
    }
}
