package org.the.force.jdbc.partition.rule;

import com.google.common.collect.Sets;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.testng.annotations.Test;
import org.the.force.jdbc.partition.TestJdbcPartitionBase;
import org.the.force.jdbc.partition.driver.SqlDialect;
import org.the.force.jdbc.partition.engine.plan.model.SqlColumnValue;
import org.the.force.jdbc.partition.resource.db.LogicDbManager;
import org.the.force.jdbc.partition.resource.table.LogicTableConfig;
import org.the.force.jdbc.partition.rule.config.DataNode;
import org.the.force.jdbc.partition.rule.config.ZKDataNode;
import org.the.force.thirdparty.druid.support.logging.Log;
import org.the.force.thirdparty.druid.support.logging.LogFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by xuji on 2017/6/19.
 */
@Test(priority = 300)
public class TestPartitionRule extends TestJdbcPartitionBase {

    private Log logger = LogFactory.getLog(TestPartitionRule.class);

    @Test(priority = 301)
    public void test2() throws Exception {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(500, 3);
        CuratorFramework curatorFramework =
            CuratorFrameworkFactory.builder().connectString(zkConnectStr).namespace(sqlDialectName + "db").connectionTimeoutMs(15000).sessionTimeoutMs(20000)
                .retryPolicy(retryPolicy).build();
        curatorFramework.start();
        DataNode zkDataNode = new ZKDataNode(null, logicDbName, curatorFramework);
        LogicDbManager logicDbConfig = new LogicDbManager(zkDataNode, SqlDialect.MySql, null, null);
        LogicTableConfig logicTableConfig = logicDbConfig.getLogicTableManager("t_user").getLogicTableConfig()[0];
        PartitionEvent partitionEvent = new PartitionEvent("t_user", PartitionEvent.EventType.INSERT, logicTableConfig.getPartitionColumnConfigs());
        partitionEvent.setPhysicDbs(logicTableConfig.getPhysicDbs());
        partitionEvent.setPartitions(logicTableConfig.getPartitions());
        TreeSet<PartitionColumnValue> set = new TreeSet<>();
        set.add(new SqlColumnValue("id", 5));
        DefaultPartitionRule defaultPartitionRule = new DefaultPartitionRule();
        SortedSet<Partition> partitions = defaultPartitionRule.selectPartitions(partitionEvent, set);
        logger.info("partions:"+ partitions.toString());
    }

    public void testSort() throws Exception {
        List<String> list = Arrays.asList("t_9", "t_8", "t_11", "t_07", "t_m_01", "t_m_2");
        Set<String> set = Sets.newTreeSet(PartitionComparator.getSingleton());
        set.addAll(list);
        logger.info(set.toString());
    }
}
