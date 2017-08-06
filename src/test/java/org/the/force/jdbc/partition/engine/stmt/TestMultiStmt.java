package org.the.force.jdbc.partition.engine.stmt;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.engine.stmt.impl.MultiSqlFactory;
import org.the.force.jdbc.partition.engine.stmt.impl.ParametricStmt;
import org.the.force.jdbc.partition.engine.value.types.IntValue;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

/**
 * Created by xuji on 2017/8/6.
 */
@Test(priority = 300)
public class TestMultiStmt {
    private static Log log = LogFactory.getLog(TestMultiStmt.class);

    public void test1() {
        String sql = "update t_order t set t.status='ok'  where t.id in (1,?,?,4,?) and t.status='1' and user_id = 0";
        ParametricStmt parametricStmt = MultiSqlFactory.getLogicSql(sql);
        log.info(parametricStmt.toString());
    }

    public void test2() {
        String sql =
            "update t_order t set t.status='ok'  where t.id in (1,?,?,4,?) and t.status='1' and user_id = 0;update t_order t set t.status='ok\\?'  where t.id in (1,?,4,?) and t.status='1' and user_id = 0;update t_order t set t.status='ok'  where t.id in (1,?,?,4,?) and t.status='1' and user_id = 0";
        ParametricStmt parametricStmt = MultiSqlFactory.getLogicSql(sql);
        log.info(parametricStmt.toString());
    }

    public void test3() {
        String sql =
            "update t_order t set t.status='update t_order set t.status=? where id>\"?\"'  where t.id in (1,?,?,4,?) and t.status='1' and user_id = 0;update t_order t set t.status='ok\\?'  where t.id in (1,?,4,?) and t.status='1' and user_id = 0;update t_order t set t.status='ok'  where t.id in (1,?,?,4,?) and t.status='1' and user_id = 0";
        ParametricStmt parametricStmt = MultiSqlFactory.getLogicSql(sql);
        log.info(parametricStmt.toString());
    }

    public void test4() {
        String sql =
            "update t_order t set t.status='update t_order set t.status=? where id>\"other str ? other str\"'  where t.id in (1,?,?,4,?) and t.status='1' and user_id = 0;update t_order t set t.status='ok\\?'  where t.id in (1,?,4,?) and t.status='1' and user_id = 0;update t_order t set t.status='ok'  where t.id in (1,?,?,4,?) and t.status='1' and user_id = 0";
        ParametricStmt parametricStmt = MultiSqlFactory.getLogicSql(sql);
        log.info(parametricStmt.toString());
    }

    public void testParamSet1() {
        String sql =
            "update t_order t set t.status='ok'  where t.id in (1,?,?,4,?) and t.status='1' and user_id = 0;update t_order t set t.status='ok'  where t.id in (1,?,4,?) and t.status='1' and user_id = 0;update t_order t set t.status='ok'  where t.id in (1,?,?,4,?) and t.status='1' and user_id = 0; ";
        ParametricStmt parametricStmt = MultiSqlFactory.getLogicSql(sql);
        log.info(parametricStmt.toString());
        parametricStmt.setParameter(1, new IntValue(2));
        Assert.assertTrue(parametricStmt.getSqlParameter(0).getValue().equals(2));
        IntValue intValue = new IntValue(4);
        parametricStmt.setParameter(4, intValue);
        Assert.assertTrue(parametricStmt.getSqlParameter(3).getValue().equals(4));
        intValue = new IntValue(5);
        parametricStmt.setParameter(5, intValue);
        Assert.assertTrue(parametricStmt.getSqlParameter(4).getValue().equals(5));
        parametricStmt.setParameter(6, new IntValue(6));
        Assert.assertTrue(parametricStmt.getSqlParameter(5).getValue().equals(6));

        parametricStmt.setParameter(8, new IntValue(8));
        Assert.assertTrue(parametricStmt.getSqlParameter(7).getValue().equals(8));
    }

    public void testParamSet2() {
        String sql =
            "update t_order t set t.status='ok'  where t.id in (1,?,?,4,?) and t.status='1' and user_id = 0;update t_order t set t.status='ok'  where t.id in (1,4,4,6) and t.status='1' and user_id = 0;update t_order t set t.status='ok'  where t.id in (1,4,4,6) and t.status='1' and user_id = 0;update t_order t set t.status='ok'  where t.id in (1,?,?,4,?) and t.status='1' and user_id = 0; ";
        ParametricStmt parametricStmt = MultiSqlFactory.getLogicSql(sql);
        log.info(parametricStmt.toString());
        parametricStmt.setParameter(1, new IntValue(2));
        Assert.assertTrue(parametricStmt.getSqlParameter(0).getValue().equals(2));
        IntValue intValue = new IntValue(4);
        parametricStmt.setParameter(4, intValue);
        Assert.assertTrue(parametricStmt.getSqlParameter(3).getValue().equals(4));
        intValue = new IntValue(5);
        parametricStmt.setParameter(5, intValue);
        Assert.assertTrue(parametricStmt.getSqlParameter(4).getValue().equals(5));

        parametricStmt.setParameter(6, new IntValue(6));
        Assert.assertTrue(parametricStmt.getSqlParameter(5).getValue().equals(6));
    }

}
