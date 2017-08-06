package org.the.force.jdbc.partition.engine.executor.factory;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestSupport;
import org.the.force.jdbc.partition.engine.executor.ast.BatchExecutableAst;
import org.the.force.jdbc.partition.engine.executor.physic.SqlExecDbNode;
import org.the.force.jdbc.partition.engine.stmt.LogicStmtConfig;
import org.the.force.jdbc.partition.engine.stmt.SqlLineExecRequest;
import org.the.force.jdbc.partition.engine.stmt.SqlLineParameter;
import org.the.force.jdbc.partition.engine.value.types.ObjectTypedValue;
import org.the.force.jdbc.partition.resource.SqlExecResource;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.executor.SqlExecutorManager;
import org.the.force.jdbc.partition.resource.executor.SqlKey;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.sql.Types;

/**
 * Created by xuji on 2017/5/20.
 */
@Test(priority = 300)
public class UpdateAstTest {

    private static Log logger = LogFactory.getLog(UpdateAstTest.class);

    LogicDbConfig logicDbConfig = TestSupport.partitionDb.ymlLogicDbConfig;
    SqlExecutorManager sqlExecutorManager = new SqlExecutorManager(logicDbConfig);
    SqlExecResource sqlExecResource = new SqlExecResource(null, null, sqlExecutorManager, logicDbConfig);
    LogicStmtConfig logicStmtConfig = new LogicStmtConfig();


    private void print(String sql, SqlLineParameter sqlLineParameter) throws Exception {
        BatchExecutableAst batchExecutableAst = (BatchExecutableAst) sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        SqlExecDbNode sqlExecDbNode = new SqlExecDbNode();
        SqlLineExecRequest sqlLineExecRequest = new SqlLineExecRequest(sqlExecResource, logicStmtConfig, sqlLineParameter);
        batchExecutableAst.addExecPhysicNode(sqlExecDbNode, sqlLineExecRequest);
        logger.info("sql解析结果" + sqlExecDbNode.toString());
    }

    public void test1() throws Exception {
        String sql = "insert into  db_order.t_order(id,user_id,status) values(?,?,?) ,(?,?,?) ON DUPLICATE KEY UPDATE t_order.user_id=?,t_order.status=?";
        SqlLineParameter sqlLineParameter = new SqlLineParameter(0, 8);
        sqlLineParameter.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        sqlLineParameter.setParameter(2, new ObjectTypedValue(8, Types.INTEGER));
        sqlLineParameter.setParameter(3, new ObjectTypedValue("ok", Types.VARCHAR));
        sqlLineParameter.setParameter(4, new ObjectTypedValue(1, Types.INTEGER));
        sqlLineParameter.setParameter(5, new ObjectTypedValue(7, Types.INTEGER));
        sqlLineParameter.setParameter(6, new ObjectTypedValue("ok", Types.VARCHAR));
        sqlLineParameter.setParameter(7, new ObjectTypedValue(8, Types.INTEGER));
        sqlLineParameter.setParameter(8, new ObjectTypedValue("ok", Types.VARCHAR));
        print(sql, sqlLineParameter);
    }

    @Test
    public void test2() throws Exception {
        String sql = "insert into  t_order(id,user_id,status) values(?,?,?)  ON DUPLICATE KEY UPDATE user_id=?,status=?";
        SqlLineParameter sqlLineParameter = new SqlLineParameter(0, 5);
        sqlLineParameter.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        sqlLineParameter.setParameter(2, new ObjectTypedValue(8, Types.INTEGER));
        sqlLineParameter.setParameter(3, new ObjectTypedValue("ok", Types.VARCHAR));
        sqlLineParameter.setParameter(4, new ObjectTypedValue(8, Types.INTEGER));
        sqlLineParameter.setParameter(5, new ObjectTypedValue("ok", Types.VARCHAR));
        print(sql, sqlLineParameter);
    }

    @Test
    public void testTableNamespace() throws Exception {
        String sql = "insert into  db_order.t_order(id,user_id,status) values(?,?,?)  ON DUPLICATE KEY UPDATE user_id=?,status=?";
        SqlLineParameter sqlLineParameter = new SqlLineParameter(0, 5);
        sqlLineParameter.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        sqlLineParameter.setParameter(2, new ObjectTypedValue(3, Types.INTEGER));
        sqlLineParameter.setParameter(3, new ObjectTypedValue("ok", Types.VARCHAR));
        sqlLineParameter.setParameter(4, new ObjectTypedValue(3, Types.INTEGER));
        sqlLineParameter.setParameter(5, new ObjectTypedValue("ok", Types.VARCHAR));
        print(sql, sqlLineParameter);
    }

    @Test
    public void testUpdate() throws Exception {
        String sql = "update t_order t set t.status=? ,t.user_id=?  where t.id=? and t.status=? ";
        SqlLineParameter sqlLineParameter = new SqlLineParameter(0, 4);
        sqlLineParameter.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        sqlLineParameter.setParameter(2, new ObjectTypedValue(1, Types.INTEGER));
        sqlLineParameter.setParameter(3, new ObjectTypedValue(0, Types.INTEGER));
        sqlLineParameter.setParameter(4, new ObjectTypedValue("ok", Types.VARCHAR));
        print(sql, sqlLineParameter);
    }


    @Test
    public void testInUpdate() throws Exception {
        String sql = "update t_order t set t.status='ok'  where t.id in (?,?,?,?,?) and t.status='1' and user_id = 0";
        SqlLineParameter sqlLineParameter = new SqlLineParameter(0, 5);
        sqlLineParameter.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        sqlLineParameter.setParameter(2, new ObjectTypedValue(8, Types.INTEGER));
        sqlLineParameter.setParameter(3, new ObjectTypedValue(12, Types.INTEGER));
        sqlLineParameter.setParameter(4, new ObjectTypedValue(3, Types.INTEGER));
        sqlLineParameter.setParameter(5, new ObjectTypedValue(2, Types.INTEGER));
        print(sql, sqlLineParameter);
    }

    @Test
    public void testInOrUpdate() throws Exception {
        String sql = "update t_order t set t.status=? ,t.user_id=?  where (t.id in (?,8,?) or t.status=?) and  id=3";
        SqlLineParameter sqlLineParameter = new SqlLineParameter(0, 5);
        sqlLineParameter.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        sqlLineParameter.setParameter(2, new ObjectTypedValue(1, Types.INTEGER));
        sqlLineParameter.setParameter(3, new ObjectTypedValue(0, Types.INTEGER));
        sqlLineParameter.setParameter(4, new ObjectTypedValue(1, Types.INTEGER));
        sqlLineParameter.setParameter(5, new ObjectTypedValue("ok", Types.VARCHAR));
        print(sql, sqlLineParameter);
    }

    @Test
    public void testInDelete() throws Exception {
        String sql = "delete from t_order t where t.id in (?,?,?) and t.status=? ";
        SqlLineParameter sqlLineParameter = new SqlLineParameter(0, 4);
        sqlLineParameter.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        sqlLineParameter.setParameter(2, new ObjectTypedValue(1, Types.INTEGER));
        sqlLineParameter.setParameter(3, new ObjectTypedValue(8, Types.INTEGER));
        sqlLineParameter.setParameter(4, new ObjectTypedValue("ok", Types.VARCHAR));
        print(sql, sqlLineParameter);
    }

    @Test
    public void testBatchParser() throws Exception {
        String sql = "update t_order t set t.status=? ,t.user_id=?  where t.id=? and t.status=? ";

        SqlLineParameter sqlLineParameter = new SqlLineParameter(0, 4);
        sqlLineParameter.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
        sqlLineParameter.setParameter(2, new ObjectTypedValue(1, Types.INTEGER));
        sqlLineParameter.setParameter(3, new ObjectTypedValue(0, Types.INTEGER));
        sqlLineParameter.setParameter(4, new ObjectTypedValue("ok", Types.VARCHAR));
        BatchExecutableAst batchExecutableAst = (BatchExecutableAst) sqlExecutorManager.getSqlExecutor(new SqlKey(sql));
        SqlExecDbNode sqlExecDbNode = new SqlExecDbNode();
        SqlLineExecRequest sqlLineExecRequest = new SqlLineExecRequest(sqlExecResource, logicStmtConfig, sqlLineParameter);
        batchExecutableAst.addExecPhysicNode(sqlExecDbNode, sqlLineExecRequest);
        for (int i = 1; i <= 9; i++) {
            sqlLineParameter = new SqlLineParameter(i, 4);
            sqlLineParameter.setParameter(1, new ObjectTypedValue(0, Types.INTEGER));
            sqlLineParameter.setParameter(2, new ObjectTypedValue(1, Types.INTEGER));
            sqlLineParameter.setParameter(3, new ObjectTypedValue(i, Types.INTEGER));
            sqlLineParameter.setParameter(4, new ObjectTypedValue("ok", Types.VARCHAR));
            sqlLineExecRequest = new SqlLineExecRequest(sqlExecResource, logicStmtConfig, sqlLineParameter);
            batchExecutableAst.addExecPhysicNode(sqlExecDbNode, sqlLineExecRequest);
        }
        logger.info("解析结果:" + sqlExecDbNode);
    }

    @Test
    public void testStaticSql() throws Exception {
        String sql = "update t_order t set t.status='ok'  where t.id in (1,2,3,4,5) and t.status='1' and user_id = 0";
        SqlLineParameter sqlLineParameter = new SqlLineParameter(0, 0);
        print(sql, sqlLineParameter);
    }
}
