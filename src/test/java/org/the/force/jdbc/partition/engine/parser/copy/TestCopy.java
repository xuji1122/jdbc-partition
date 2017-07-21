package org.the.force.jdbc.partition.engine.parser.copy;

import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.util.JdbcConstants;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Created by xuji on 2017/5/23.
 */
public class TestCopy {

    @Test
    public void test1() throws Exception {
        String sql = "select * from `t_order` t,`t_user` u where t.user_id=u.id and t.status='1' ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
            System.out.println(SQLUtils.toMySqlString(sqlStatement));
            SQLStatement obj = new SqlObjCopier("parent").copy(sqlStatement);
            System.out.println(SQLUtils.toMySqlString(obj));
        }
    }

}
