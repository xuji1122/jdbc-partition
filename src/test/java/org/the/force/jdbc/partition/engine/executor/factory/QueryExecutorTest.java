package org.the.force.jdbc.partition.engine.executor.factory;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestSupport;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.executor.SqlExecutorManager;
import org.the.force.jdbc.partition.resource.executor.SqlKey;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

/**
 * Created by xuji on 2017/5/20.
 */
@Test(priority = 300)
public class QueryExecutorTest {

    private static Log logger = LogFactory.getLog(QueryExecutorTest.class);

    LogicDbConfig logicDbConfig = TestSupport.partitionDb.ymlLogicDbConfig;

    SqlExecutorManager sqlExecutorManager;

    public QueryExecutorTest() throws Exception {
        sqlExecutorManager = new SqlExecutorManager(logicDbConfig);
    }

    public void test1() throws Exception {
        String sql = "select id,name from t_order where user_id in (?,?,?) and name=?  and id=2  and (time>? or status=?) and  1=1 order by id limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        logger.info(queryExecutor.toString());
    }

    public void test2() throws Exception {
        String sql =
            "select o.id,o.channelArray from t_order o join t_order_sku i on o.id=i.order_id where  o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and o.id in (1,2,3) and o.abc=? order by o.id limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        logger.info(queryExecutor.toString());
    }

    public void test3() throws Exception {
        String sql =
            "select o.id,o.channelArray from t_order o ,t_order_sku i  where o.id=i.order_id and o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and o.id in (1,2,3) and o.abc=? order by o.id limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        logger.info(queryExecutor.toString());
    }

    public void test4() throws Exception {
        String sql =
            "select o.id,o.channelArray from t_order o ,t_order_sku i  where o.id = i.order_id and o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and o.name in (1,2,3) and o.abc=?  and o.status in (select status from t_user) order by id limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        logger.info(queryExecutor.toString());
    }
    public void test5() throws Exception {
        String sql =
            "select o.id,o.channelArray from t_user t,t_order o ,t_order_sku i  where t.id=o.user_id and o.id = i.order_id and o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and t.id in ('a','b'+'c','d') and o.abc=?  and o.status in (select status from t_user) order by id limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        logger.info(queryExecutor.toString());
    }

    public void test6() throws Exception {
        String sql =
            "select o.id,o.channelArray from (select id from t_user where id=? ) t,t_order o ,t_order_sku i  where t.id=o.user_id and o.id = i.order_id and o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and o.user_id in (1,2,3) and o.abc=?  and o.status in (select status from t_user) order by id limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        logger.info(queryExecutor.toString());
    }

    public void test7() throws Exception {
        String sql =
            "select o.id,o.channelArray from ( select  t0.id,t0.name from (select id,name from t_user where name =? order by id) t0 where 1=1 ) t,t_order o ,t_order_sku i  where t.id=o.user_id and o.id = i.order_id and o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and  o.user_id in (1,2,3) and o.abc=?  and o.status in (select status from t_user) and exits (select name from t_user) order by i.id limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        logger.info(queryExecutor.toString());
    }

    //子查询
    public void test10() throws Exception {
        String sql =
            "select o.id,o.channelArray from (select id from t_user where id=? ) t,t_order o ,t_order_sku i  where t.id=o.user_id and o.id = i.order_id and o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and  o.user_id in (1,2,3) and o.abc=?  and o.status in (select status from t_user) and exits (select name from t_user) order by i.id limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        logger.info(queryExecutor.toString());
    }

    //子查询作为tableSource
    public void test20() throws Exception {
        String sql =
            "select t.id,t.channelArray,t.abc from ( select  t0.id,t0.name,t0.abc from (select id,name,abc from t_user where name =? order by id) t0 where 1=1 ) t   where  (t.status=1 or t.status=2  )  and  t.id in (1,2,3) and t.abc=?   order by t.id  limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        logger.info(queryExecutor.toString());
    }

    public void test21() throws Exception {
        String sql =
            "select t.id,t.channelArray,t.abc from ( select  t0.id,t0.name,t0.abc from (select id,name,abc from t_user where name =? order by id) t0 where 1=1 limit 2,10 ) t   where  (t.status=1 or t.status=2  )  and  t.id in (1,2,3) and t.abc=?   order by t.id  limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        logger.info(queryExecutor.toString());
    }
}
