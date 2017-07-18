package org.the.force.thirdparty.druid;

import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.util.JdbcConstants;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.engine.executor.factory.SqlExecutorFactory;

import java.util.List;

/**
 * Created by xuji on 2017/5/17.
 */
@Test
public class UpdateStatementTest {


    public void testStartupVisitor() {
        String sql = "blockquery * from t_order o join t_order_item i on t.id=o.order_id where id=? ";
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlExecutorFactory visitor = new SqlExecutorFactory(null);
        stmt.accept(visitor);
        sql = "insert into  t_order(id) values(?),(?)";
        stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        visitor = new SqlExecutorFactory(null);
        stmt.accept(visitor);
    }

    public void testUpdateStartupVisitor() {
        String sql = "update t_order set status=1 where id=1;update t_order set status=0 where id=2 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SqlExecutorFactory visitor = new SqlExecutorFactory(null);
            stmts.get(i).accept(visitor);
        }
    }

    public void testSQLName() {
        String sql = "update t_order  t set t_order.status=1 where t.id=1 ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testAndOr() {
        String sql = "update t_order  t set t_order.status=1 where t.order_id=1  and  (t.user_id=2 or t.status='3') and t.order_id%3=4 and t.id in (5,6,7)";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testConditionJoin() {
        String sql = "blockquery * from `t_order` t,`t_user` u where t.user_id=u.id and t.status='1' ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

    public void testUpdate() {
        String sql = "update t_order set t.status=? ,t.name=?  where t.order_id=3 and t.status='1' ";
        List<SQLStatement> stmts = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL);
        for (int i = 0; i < stmts.size(); i++) {
            SQLStatement sqlStatement = stmts.get(i);
        }
    }

}
