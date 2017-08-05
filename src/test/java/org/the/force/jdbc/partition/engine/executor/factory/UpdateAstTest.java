package org.the.force.jdbc.partition.engine.executor.factory;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestSupport;
import org.the.force.jdbc.partition.engine.executor.ast.BatchExecutableAst;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecCommandDbReceiver;
import org.the.force.jdbc.partition.engine.stmt.LogicStmt;
import org.the.force.jdbc.partition.engine.value.types.ObjectTypedValue;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.thirdparty.druid.sql.SQLUtils;
import org.the.force.thirdparty.druid.sql.ast.SQLStatement;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;
import org.the.force.thirdparty.druid.util.JdbcConstants;

import java.sql.Types;

/**
 * Created by xuji on 2017/5/20.
 */
@Test(priority = 300)
public class UpdateAstTest {

    private static Log logger = LogFactory.getLog(UpdateAstTest.class);

    LogicDbConfig logicDbConfig = TestSupport.partitionDb.ymlLogicDbConfig;

    public UpdateAstTest() throws Exception {
    }

    public void test1() throws Exception {
        String sql = "insert into  db_order.t_order(id,user_id,status) values(?,?,?) ,(?,?,?) ON DUPLICATE KEY UPDATE t_order.user_id=?,t_order.status=?";
        LogicStmt logicLogicStmt = new LogicStmt(sql);
        logicLogicStmt.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        logicLogicStmt.setParameter(2, new ObjectTypedValue(8, Types.INTEGER));
        logicLogicStmt.setParameter(3, new ObjectTypedValue("ok", Types.VARCHAR));
        logicLogicStmt.setParameter(4, new ObjectTypedValue(1, Types.INTEGER));
        logicLogicStmt.setParameter(5, new ObjectTypedValue(7, Types.INTEGER));
        logicLogicStmt.setParameter(6, new ObjectTypedValue("ok", Types.VARCHAR));
        logicLogicStmt.setParameter(7, new ObjectTypedValue(8, Types.INTEGER));
        logicLogicStmt.setParameter(8, new ObjectTypedValue("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlExecutorFactory visitor = new SqlExecutorFactory(logicDbConfig);
        stmt.accept(visitor);
        SqlExecCommandDbReceiver dbExecutorRouter = new SqlExecCommandDbReceiver();
        ((BatchExecutableAst) visitor.getSqlExecutor()).addExecPhysicNode(dbExecutorRouter, logicLogicStmt);
        logger.info("sql解析结果" + dbExecutorRouter.toString());
    }

    @Test
    public void test2() throws Exception {
        String sql = "insert into  t_order(order_id,user_id,status) values(?,?,?)  ON DUPLICATE KEY UPDATE user_id=?,status=?";
        LogicStmt logicLogicStmt = new LogicStmt();
        logicLogicStmt.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        logicLogicStmt.setParameter(2, new ObjectTypedValue(8, Types.INTEGER));
        logicLogicStmt.setParameter(3, new ObjectTypedValue("ok", Types.VARCHAR));
        logicLogicStmt.setParameter(4, new ObjectTypedValue(8, Types.INTEGER));
        logicLogicStmt.setParameter(5, new ObjectTypedValue("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlExecutorFactory visitor = new SqlExecutorFactory(logicDbConfig);
        stmt.accept(visitor);
        //        visitor.getSqlExecutor().addExecPhysicNode(logicLogicStmt);
        //        logger.info("解析结果：{}" + visitor.getSqlExecutor().getdbExecutorRouter());
    }

    @Test
    public void testTableNamespace() throws Exception {
        String sql = "insert into  db_order.t_order(id,user_id,status) values(?,?,?)  ON DUPLICATE KEY UPDATE user_id=?,status=?";
        LogicStmt logicLogicStmt = new LogicStmt();
        logicLogicStmt.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        logicLogicStmt.setParameter(2, new ObjectTypedValue(3, Types.INTEGER));
        logicLogicStmt.setParameter(3, new ObjectTypedValue("ok", Types.VARCHAR));
        logicLogicStmt.setParameter(4, new ObjectTypedValue(3, Types.INTEGER));
        logicLogicStmt.setParameter(5, new ObjectTypedValue("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlExecutorFactory visitor = new SqlExecutorFactory(logicDbConfig);
        stmt.accept(visitor);
        SqlExecCommandDbReceiver dbExecutorRouter = new SqlExecCommandDbReceiver();
        ((BatchExecutableAst) visitor.getSqlExecutor()).addExecPhysicNode(dbExecutorRouter, logicLogicStmt);
        logger.info("sql解析结果" + dbExecutorRouter.toString());
    }

    @Test
    public void testUpdate() throws Exception {
        String sql = "update t_order t set t.status=? ,t.user_id=?  where t.id=? and t.status=? ";
        LogicStmt logicLogicStmt = new LogicStmt();
        logicLogicStmt.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        logicLogicStmt.setParameter(2, new ObjectTypedValue(1, Types.INTEGER));
        logicLogicStmt.setParameter(3, new ObjectTypedValue(0, Types.INTEGER));
        logicLogicStmt.setParameter(4, new ObjectTypedValue("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlExecutorFactory visitor = new SqlExecutorFactory(logicDbConfig);
        stmt.accept(visitor);
        SqlExecCommandDbReceiver dbExecutorRouter = new SqlExecCommandDbReceiver();
        ((BatchExecutableAst) visitor.getSqlExecutor()).addExecPhysicNode(dbExecutorRouter, logicLogicStmt);
        logger.info("sql解析结果" + dbExecutorRouter.toString());
    }


    @Test
    public void testInUpdate() throws Exception {
        String sql = "update t_order t set t.status='ok'  where t.id in (?,?,?,?,?) and t.status='1' and user_id = 0";
        LogicStmt logicLogicStmt = new LogicStmt();
        logicLogicStmt.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        logicLogicStmt.setParameter(2, new ObjectTypedValue(8, Types.INTEGER));
        logicLogicStmt.setParameter(3, new ObjectTypedValue(12, Types.INTEGER));
        logicLogicStmt.setParameter(4, new ObjectTypedValue(3, Types.INTEGER));
        logicLogicStmt.setParameter(5, new ObjectTypedValue(2, Types.INTEGER));
        //logicLogicStmt.setParameter(5, new ObjectTypedValue("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlExecutorFactory visitor = new SqlExecutorFactory(logicDbConfig);
        stmt.accept(visitor);
        SqlExecCommandDbReceiver dbExecutorRouter = new SqlExecCommandDbReceiver();
        ((BatchExecutableAst) visitor.getSqlExecutor()).addExecPhysicNode(dbExecutorRouter, logicLogicStmt);
        logger.info("sql解析结果" + dbExecutorRouter.toString());

    }

    @Test
    public void testInOrUpdate() throws Exception {
        String sql = "update t_order t set t.status=? ,t.user_id=?  where (t.id in (?,8,?) or t.status=?) and  id=3";
        LogicStmt logicLogicStmt = new LogicStmt();
        logicLogicStmt.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        logicLogicStmt.setParameter(2, new ObjectTypedValue(1, Types.INTEGER));
        logicLogicStmt.setParameter(3, new ObjectTypedValue(0, Types.INTEGER));
        logicLogicStmt.setParameter(4, new ObjectTypedValue(1, Types.INTEGER));
        logicLogicStmt.setParameter(5, new ObjectTypedValue("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlExecutorFactory visitor = new SqlExecutorFactory(logicDbConfig);
        stmt.accept(visitor);
        SqlExecCommandDbReceiver dbExecutorRouter = new SqlExecCommandDbReceiver();
        ((BatchExecutableAst) visitor.getSqlExecutor()).addExecPhysicNode(dbExecutorRouter, logicLogicStmt);
        logger.info("sql解析结果" + dbExecutorRouter.toString());
    }

    @Test
    public void testInDelete() throws Exception {
        String sql = "delete from t_order t where t.id in (?,?,?) and t.status=? ";
        LogicStmt logicLogicStmt = new LogicStmt();
        logicLogicStmt.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        logicLogicStmt.setParameter(2, new ObjectTypedValue(1, Types.INTEGER));
        logicLogicStmt.setParameter(3, new ObjectTypedValue(8, Types.INTEGER));
        logicLogicStmt.setParameter(4, new ObjectTypedValue("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlExecutorFactory visitor = new SqlExecutorFactory(logicDbConfig);
        stmt.accept(visitor);
        SqlExecCommandDbReceiver dbExecutorRouter = new SqlExecCommandDbReceiver();

        ((BatchExecutableAst) visitor.getSqlExecutor()).addExecPhysicNode(dbExecutorRouter, logicLogicStmt);
        logger.info("sql解析结果" + dbExecutorRouter.toString());
    }

    @Test
    public void testBatchParser() throws Exception {
        String sql = "update t_order t set t.status=? ,t.user_id=?  where t.id=? and t.status=? ";

        LogicStmt logicLogicStmt = new LogicStmt();
        logicLogicStmt.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        logicLogicStmt.setParameter(2, new ObjectTypedValue(1, Types.INTEGER));
        logicLogicStmt.setParameter(3, new ObjectTypedValue(0, Types.INTEGER));
        logicLogicStmt.setParameter(4, new ObjectTypedValue("ok", Types.VARCHAR));
        SQLStatement stmt = SQLUtils.parseStatements(sql, JdbcConstants.MYSQL).get(0);
        SqlExecutorFactory visitor = new SqlExecutorFactory(logicDbConfig);
        stmt.accept(visitor);
        BatchExecutableAst batchExecutableAst = (BatchExecutableAst) visitor.getSqlExecutor();
        SqlExecCommandDbReceiver dbExecutorRouter = new SqlExecCommandDbReceiver();
        for (int i = 1; i <= 9; i++) {
            logicLogicStmt.addLineNumber();
            logicLogicStmt.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
            logicLogicStmt.setParameter(2, new ObjectTypedValue(1, Types.INTEGER));
            logicLogicStmt.setParameter(3, new ObjectTypedValue(i, Types.INTEGER));
            logicLogicStmt.setParameter(4, new ObjectTypedValue("ok", Types.VARCHAR));
            batchExecutableAst.addExecPhysicNode(dbExecutorRouter, logicLogicStmt);
        }
        logger.info("解析结果:" + dbExecutorRouter);
    }
}
