package org.the.force.jdbc.partition.engine.plan;

import org.druid.sql.SQLUtils;
import org.druid.sql.ast.SQLStatement;
import org.druid.util.JdbcConstants;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcPartitionBase;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.parameter.ObjectSqlParameter;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.db.LogicDbManager;
import org.the.force.jdbc.partition.rule.config.DataNode;
import org.the.force.jdbc.partition.rule.config.ZKDataNode;

import java.sql.Types;

/**
 * Created by xuji on 2017/5/20.
 */
@Test(priority = 300)
public class UpdatePlanTest extends TestJdbcPartitionBase {
    private static Logger logger = LoggerFactory.getLogger(UpdatePlanTest.class);
    CuratorFramework curatorFramework = null;
    DataNode zkDataNode = null;
    LogicDbConfig logicDbConfig = null;

    public UpdatePlanTest() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(500, 3);
        curatorFramework =
            CuratorFrameworkFactory.builder().connectString(zkConnectStr).namespace(zkRootPath).connectionTimeoutMs(15000).sessionTimeoutMs(20000).retryPolicy(retryPolicy).build();
        curatorFramework.start();
        zkDataNode = new ZKDataNode(null, logicDbName, curatorFramework);
        logicDbConfig = new LogicDbManager(zkDataNode, SqlDialect.MySql, null, null);

    }

    public void test1() throws Exception {
        String sql = "insert into  db_order.t_order(id,user_id,status) values(?,?,?) ,(?,?,?) ON DUPLICATE KEY UPDATE t_order.user_id=?,t_order.status=?";
        LogicSqlParameterHolder logicLogicSqlParameterHolder = new LogicSqlParameterHolder();
        logicLogicSqlParameterHolder.setParameter(1, new ObjectSqlParameter(0, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(2, new ObjectSqlParameter(8, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(3, new ObjectSqlParameter("ok", Types.VARCHAR));
        logicLogicSqlParameterHolder.setParameter(4, new ObjectSqlParameter(1, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(5, new ObjectSqlParameter(7, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(6, new ObjectSqlParameter("ok", Types.VARCHAR));
        logicLogicSqlParameterHolder.setParameter(7, new ObjectSqlParameter(8, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(8, new ObjectSqlParameter("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlPlanMatcher visitor = new SqlPlanMatcher(logicDbConfig);
        stmt.accept(visitor);
        PhysicDbExecutor dbExecutorRouter = new PhysicDbExecutor();
        ((PhysicSqlPlan) visitor.getSqlPlan()).setParameters(dbExecutorRouter, logicLogicSqlParameterHolder);
        logger.info("sql解析结果{}", dbExecutorRouter.toString());
    }

    @Test
    public void test2() throws Exception {
        String sql = "insert into  t_order(order_id,user_id,status) values(?,?,?)  ON DUPLICATE KEY UPDATE user_id=?,status=?";
        LogicSqlParameterHolder logicLogicSqlParameterHolder = new LogicSqlParameterHolder();
        logicLogicSqlParameterHolder.setParameter(1, new ObjectSqlParameter(0, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(2, new ObjectSqlParameter(8, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(3, new ObjectSqlParameter("ok", Types.VARCHAR));
        logicLogicSqlParameterHolder.setParameter(4, new ObjectSqlParameter(8, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(5, new ObjectSqlParameter("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlPlanMatcher visitor = new SqlPlanMatcher(logicDbConfig);
        stmt.accept(visitor);
        //        visitor.getSqlPlan().setParameters(logicLogicSqlParameterHolder);
        //        logger.info("解析结果：{}" + visitor.getSqlPlan().getdbExecutorRouter());
    }

    @Test
    public void testTableNamespace() throws Exception {
        String sql = "insert into  db_order.t_order(id,user_id,status) values(?,?,?)  ON DUPLICATE KEY UPDATE user_id=?,status=?";
        LogicSqlParameterHolder logicLogicSqlParameterHolder = new LogicSqlParameterHolder();
        logicLogicSqlParameterHolder.setParameter(1, new ObjectSqlParameter(0, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(2, new ObjectSqlParameter(3, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(3, new ObjectSqlParameter("ok", Types.VARCHAR));
        logicLogicSqlParameterHolder.setParameter(4, new ObjectSqlParameter(3, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(5, new ObjectSqlParameter("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlPlanMatcher visitor = new SqlPlanMatcher(logicDbConfig);
        stmt.accept(visitor);
        PhysicDbExecutor dbExecutorRouter = new PhysicDbExecutor();
        ((PhysicSqlPlan) visitor.getSqlPlan()).setParameters(dbExecutorRouter, logicLogicSqlParameterHolder);
        logger.info("sql解析结果{}", dbExecutorRouter.toString());
    }

    @Test
    public void testUpdate() throws Exception {
        String sql = "update t_order t set t.status=? ,t.user_id=?  where t.id=? and t.status=? ";
        LogicSqlParameterHolder logicLogicSqlParameterHolder = new LogicSqlParameterHolder();
        logicLogicSqlParameterHolder.setParameter(1, new ObjectSqlParameter(0, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(2, new ObjectSqlParameter(1, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(3, new ObjectSqlParameter(0, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(4, new ObjectSqlParameter("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlPlanMatcher visitor = new SqlPlanMatcher(logicDbConfig);
        stmt.accept(visitor);
        PhysicDbExecutor dbExecutorRouter = new PhysicDbExecutor();
        ((PhysicSqlPlan) visitor.getSqlPlan()).setParameters(dbExecutorRouter, logicLogicSqlParameterHolder);
        logger.info("sql解析结果{}", dbExecutorRouter.toString());
    }


    @Test
    public void testInUpdate() throws Exception {
        String sql = "update t_order t set t.status='ok'  where t.id in (?,?,?,?,?) and t.status='1' and user_id = 0";
        LogicSqlParameterHolder logicLogicSqlParameterHolder = new LogicSqlParameterHolder();
        logicLogicSqlParameterHolder.setParameter(1, new ObjectSqlParameter(0, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(2, new ObjectSqlParameter(8, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(3, new ObjectSqlParameter(12, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(4, new ObjectSqlParameter(3, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(5, new ObjectSqlParameter(2, Types.INTEGER));
        //logicLogicSqlParameterHolder.setParameter(5, new ObjectSqlParameter("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlPlanMatcher visitor = new SqlPlanMatcher(logicDbConfig);
        stmt.accept(visitor);
        PhysicDbExecutor dbExecutorRouter = new PhysicDbExecutor();
        ((PhysicSqlPlan) visitor.getSqlPlan()).setParameters(dbExecutorRouter, logicLogicSqlParameterHolder);
        logger.info("sql解析结果{}", dbExecutorRouter.toString());

    }

    @Test
    public void testInOrUpdate() throws Exception {
        String sql = "update t_order t set t.status=? ,t.user_id=?  where (t.id in (?,8,?) or t.status=?) and  id=3";
        LogicSqlParameterHolder logicLogicSqlParameterHolder = new LogicSqlParameterHolder();
        logicLogicSqlParameterHolder.setParameter(1, new ObjectSqlParameter(0, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(2, new ObjectSqlParameter(1, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(3, new ObjectSqlParameter(0, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(4, new ObjectSqlParameter(1, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(5, new ObjectSqlParameter("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlPlanMatcher visitor = new SqlPlanMatcher(logicDbConfig);
        stmt.accept(visitor);
        PhysicDbExecutor dbExecutorRouter = new PhysicDbExecutor();
        ((PhysicSqlPlan) visitor.getSqlPlan()).setParameters(dbExecutorRouter, logicLogicSqlParameterHolder);
        logger.info("sql解析结果{}", dbExecutorRouter.toString());
    }

    @Test
    public void testInDelete() throws Exception {
        String sql = "delete from t_order t where t.id in (?,?,?) and t.status=? ";
        LogicSqlParameterHolder logicLogicSqlParameterHolder = new LogicSqlParameterHolder();
        logicLogicSqlParameterHolder.setParameter(1, new ObjectSqlParameter(0, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(2, new ObjectSqlParameter(1, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(3, new ObjectSqlParameter(8, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(4, new ObjectSqlParameter("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlPlanMatcher visitor = new SqlPlanMatcher(logicDbConfig);
        stmt.accept(visitor);
        PhysicDbExecutor dbExecutorRouter = new PhysicDbExecutor();

        ((PhysicSqlPlan) visitor.getSqlPlan()).setParameters(dbExecutorRouter, logicLogicSqlParameterHolder);
        logger.info("sql解析结果{}", dbExecutorRouter.toString());
    }

    @Test
    public void testBatchParser() throws Exception {
        String sql = "update t_order t set t.status=? ,t.user_id=?  where t.id=? and t.status=? ";

        LogicSqlParameterHolder logicLogicSqlParameterHolder = new LogicSqlParameterHolder();
        logicLogicSqlParameterHolder.setParameter(1, new ObjectSqlParameter(0, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(2, new ObjectSqlParameter(1, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(3, new ObjectSqlParameter(0, Types.INTEGER));
        logicLogicSqlParameterHolder.setParameter(4, new ObjectSqlParameter("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlPlanMatcher visitor = new SqlPlanMatcher(logicDbConfig);
        stmt.accept(visitor);
        PhysicSqlPlan physicSqlPlan = (PhysicSqlPlan) visitor.getSqlPlan();
        PhysicDbExecutor dbExecutorRouter = new PhysicDbExecutor();
        for (int i = 1; i <= 9; i++) {
            logicLogicSqlParameterHolder.addLineNumber();
            logicLogicSqlParameterHolder.setParameter(1, new ObjectSqlParameter(0, Types.INTEGER));
            logicLogicSqlParameterHolder.setParameter(2, new ObjectSqlParameter(1, Types.INTEGER));
            logicLogicSqlParameterHolder.setParameter(3, new ObjectSqlParameter(i, Types.INTEGER));
            logicLogicSqlParameterHolder.setParameter(4, new ObjectSqlParameter("ok", Types.VARCHAR));
            physicSqlPlan.setParameters(dbExecutorRouter,logicLogicSqlParameterHolder);
        }
        logger.info("解析结果：{}", dbExecutorRouter);
    }
}
