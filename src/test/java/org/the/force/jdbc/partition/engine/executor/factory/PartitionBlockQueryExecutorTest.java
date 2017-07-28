package org.the.force.jdbc.partition.engine.executor.factory;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestSupport;
import org.the.force.jdbc.partition.engine.executor.QueryExecutor;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.executor.SqlExecutorManager;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

/**
 * Created by xuji on 2017/7/25.
 */
@Test(priority = 300)
public class PartitionBlockQueryExecutorTest {
    private static Log logger = LogFactory.getLog(PartitionBlockQueryExecutorTest.class);

    LogicDbConfig logicDbConfig = TestSupport.partitionDb.ymlLogicDbConfig;

    SqlExecutorManager sqlExecutorManager;

    public PartitionBlockQueryExecutorTest() throws Exception {
        sqlExecutorManager = new SqlExecutorManager(logicDbConfig);
    }

    public void test1() throws Exception {
        String sql = "select name,status from t_order where user_id in (?,?,?) and status='ok'  order by id limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutor.toString());
    }

    public void test2() throws Exception {
        String sql = "select channelArray,max(total_price) as total_price,avg(total_price) from t_order where user_id in (?,?,?) and status='ok'  group by t_order.user_id,channelArray order by total_price limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutor.toString());
    }

    public void test3() throws Exception {
        String sql = "select channelArray,max(total_price) as total_price,avg(total_price) as avg_price from t_order where user_id in (?,?,?) and status='ok'  group by user_id,channelArray order by user_id desc,avg_price limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutor.toString());
    }

    public void test4() throws Exception {
        String sql = "select channelArray,max(total_price) as total_price,avg(total_price) as avg_price from t_order where user_id in (?,?,?) and status='ok'  group by channelArray,t_order.user_id order by user_id desc,avg_price limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutor.toString());
    }

    public void test5() throws Exception {
        String sql = "select channelArray,max(total_price) as total_price,avg(total_price) as avg_price from t_order where user_id in (?,?,?) and status='ok'  group by channelArray,t_order.user_id having sum(total_price) > avg(total_price) order by user_id desc,avg_price limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutor.toString());
    }

    public void test6() throws Exception {
        String sql = "select distinct channelArray,max(total_price) as total_price,avg(total_price) as avg_price from t_order where user_id in (?,?,?) and status='ok'  group by channelArray,t_order.user_id having sum(total_price) > avg(total_price) order by user_id desc,avg_price limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutor.toString());
    }

    public void test7() throws Exception {
        String sql = "select distinct name,status,id from t_order where user_id in (?,?,?) and status='ok'  order by id desc limit 20 ";
        QueryExecutor queryExecutor = (QueryExecutor) sqlExecutorManager.getSqlExecutor(sql);
        logger.info(queryExecutor.toString());
    }
}
