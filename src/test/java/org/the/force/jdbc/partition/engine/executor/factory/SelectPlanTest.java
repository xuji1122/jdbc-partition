package org.the.force.jdbc.partition.engine.executor.factory;

import org.the.force.jdbc.partition.TestJdbcPartitionBase;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.db.LogicDbManager;
import org.the.force.jdbc.partition.resource.executor.SqlExecutorManager;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

/**
 * Created by xuji on 2017/5/20.
 */
//@Test(priority = 300)
public class SelectPlanTest extends TestJdbcPartitionBase {

    private static Log logger = LogFactory.getLog(SelectPlanTest.class);

    LogicDbConfig logicDbConfig = null;

    SqlExecutorManager sqlExecutorManager;

    public SelectPlanTest() throws Exception {
        logicDbConfig = new LogicDbManager(jsonDbDataNode, SqlDialect.MySql, paramStr, propInfo);
        sqlExecutorManager = new SqlExecutorManager(logicDbConfig);
    }

    public void test1() throws Exception {
        String sql = "executor id,name from t_order where user_id in (?,?,?) and name=?  and id=2  and (time>? or status=?) and  1=1 order by id limit 20 ";
        QueryExecutorFactory queryExecutorFactory = (QueryExecutorFactory) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutorFactory.toString());
    }

    public void test2() throws Exception {
        String sql =
            "executor id,channel from t_order o join t_order_sku i on o.id=i.order_id where  o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and o.name in (1,2,3) and o.abc=? order by id limit 20 ";
        QueryExecutorFactory queryExecutorFactory = (QueryExecutorFactory) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutorFactory.toString());
    }

    public void test3() throws Exception {
        String sql =
            "executor id,channel from t_order o ,t_order_sku i  where o.id=i.order_id and o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and o.name in (1,2,3) and o.abc=? order by id limit 20 ";
        QueryExecutorFactory queryExecutorFactory = (QueryExecutorFactory) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutorFactory.toString());
    }

    public void test4() throws Exception {
        String sql =
            "executor id,channel from t_order o ,t_order_sku i  where o.id = i.order_id and o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and o.name in (1,2,3) and o.abc=?  and o.status in (executor status from t_user) order by id limit 20 ";
        QueryExecutorFactory queryExecutorFactory = (QueryExecutorFactory) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutorFactory.toString());
    }
    public void test5() throws Exception {
        String sql =
            "executor o.id,o.channel from t_user t,t_order o ,t_order_sku i  where t.id=o.user_id and o.id = i.order_id and o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and o.name in (1,2,3) and o.abc=?  and o.status in (executor status from t_user) order by id limit 20 ";
        QueryExecutorFactory queryExecutorFactory = (QueryExecutorFactory) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutorFactory.toString());
    }

    public void test6() throws Exception {
        String sql =
            "executor o.id,o.channel from (executor id from t_user where id=? ) t,t_order o ,t_order_sku i  where t.id=o.user_id and o.id = i.order_id and o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and o.name in (1,2,3) and o.abc=?  and o.status in (executor status from t_user) order by id limit 20 ";
        QueryExecutorFactory queryExecutorFactory = (QueryExecutorFactory) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutorFactory.toString());
    }
    public void test7() throws Exception {
        String sql =
            "executor o.id,o.channel from (executor id from t_user where id=? ) t,t_order o ,t_order_sku i  where t.id=o.user_id and o.id = i.order_id and o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and o.name in (1,2,3) and o.abc=?  and o.status in (executor status from t_user) and exits (executor name from t_user) order by i.id limit 20 ";
        QueryExecutorFactory queryExecutorFactory = (QueryExecutorFactory) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutorFactory.toString());
    }
}
