package org.the.force.thirdparty.druid;

import org.testng.annotations.Test;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.util.JdbcConstants;

import java.util.List;

/**
 * Created by xuji on 2017/7/20.
 */
@Test
public class SqlValueTest {

    public void testValue1() {
        String sql = "select 1.5,5,900000000000000000000 from t_order where atime>'2015-00-01 00:00:00' and tid is null  ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }
}
