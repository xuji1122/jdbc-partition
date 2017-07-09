package org.the.force.jdbc.partition.engine.executor.plan;

import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcPartitionBase;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.engine.LogicSqlParameterHolder;
import org.the.force.jdbc.partition.engine.executor.physic.PhysicDbExecutor;
import org.the.force.jdbc.partition.engine.parameter.ObjectSqlParameter;
import org.the.force.jdbc.partition.engine.parser.elements.ExprSqlTable;
import org.the.force.jdbc.partition.resource.db.LogicDbConfig;
import org.the.force.jdbc.partition.resource.db.LogicDbManager;
import org.the.force.jdbc.partition.resource.sql.SqlExecutionPlanManager;
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
public class SelectPlanTest extends TestJdbcPartitionBase {

    private static Log logger = LogFactory.getLog(SelectPlanTest.class);

    LogicDbConfig logicDbConfig = null;

    SqlExecutionPlanManager sqlExecutionPlanManager;

    public SelectPlanTest() throws Exception {
        logicDbConfig = new LogicDbManager(jsonDbDataNode, SqlDialect.MySql, paramStr, propInfo);
        sqlExecutionPlanManager = new SqlExecutionPlanManager(logicDbConfig);
    }

    public void test1() throws Exception {
        String sql = "select id,name from t_order where user_id in (?,?,?) and name=?  and id=2  and (time>? or status=?) and  1=1 order by id limit 20 ";
        QueryPlan queryPlan = (QueryPlan) sqlExecutionPlanManager.getSqlExecutionPlan(sql);
        logger.info(queryPlan.toString());
    }

    public void testTableConditionParser2() throws Exception {
        String sql =
            "select id,channel from t_order o join t_order_sku i on o.id=i.order_id where  o.id>0  and  (i.time>? or i.status=?) and (o.status=1 or o.status=2  )  and o.name in (1,2,3) and o.abc=? order by id limit 20 ";
        QueryPlan queryPlan = (QueryPlan) sqlExecutionPlanManager.getSqlExecutionPlan(sql);
        logger.info(queryPlan.toString());
    }
}
